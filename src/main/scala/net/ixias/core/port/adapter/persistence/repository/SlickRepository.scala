/*
 * This file is part of the IxiaS services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package net.ixias
package core.port.adapter.persistence.repository

import scala.util.Failure
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import slick.dbio.{ DBIOAction, NoStream }
import slick.driver.JdbcProfile
import com.typesafe.config.Config

import core.domain.model.Entity
import core.port.adapter.persistence.lifted._
import core.port.adapter.persistence.backend.SlickBackend
import core.port.adapter.persistence.io.EntityIOActionContext

/**
 * The base repository for persistence with using the Slick library.
 */
trait SlickRepository[K, E <: Entity[K], P <: JdbcProfile]
    extends Repository[K, E] with SlickProfile[P]

/**
 * The profile for persistence with using the Slick library.
 */
trait SlickProfile[P <: JdbcProfile] extends Profile
    with SlickActionComponent[P] with ExtensionMethodConversions { self =>

  type This >: this.type <: SlickProfile[P]
  /** The back-end type required by this profile */
  type Backend  = SlickBackend[P]
  /** The type of database objects. */
  type Database = Backend#Database
  /** The type of the context used for running repository Actions */
  type Context =  EntityIOActionContext

  /** The configured driver. */
  val driver: P

  /** The back-end implementation for this profile */
  val backend = new SlickBackend[P] {}

  /** The API for using the utility methods with a single import statement.
    * This provides the repository's implicits, the Database connections,
    * and commonly types and objects. */
  trait API extends super.API with driver.API
      with SlickColumnOptionOps
      with SlickColumnTypeOps[P] {
    lazy val driver = self.driver
  }
  val api: API = new API {}

  /** Run the supplied function with a database object by using pool database session. */
  def withDatabase[T](dsn: String)(f: Database => Future[T])(implicit ctx: Context): Future[T] =
    (for {
      db    <- Future.fromTry(backend.getDatabase(driver, dsn))
      value <- f(db)
    } yield value) andThen {
      case Failure(ex) => actionLogger.error("The database action failed. dsn=" + dsn, ex)
    }

  /** Run an Action synchronously and return the result as a Try. */
  def runWithDatabase[R, T](dsn: String)(action: => DBIOAction[R, NoStream, Nothing])
    (implicit ctx: Context, codec: R => T): Future[T] =
    (for {
      db    <- Future.fromTry(backend.getDatabase(driver, dsn))
      value <- db.run(action).map(codec)
    } yield value) andThen {
      case Failure(ex) => actionLogger.error("The database action failed. dsn=" + dsn, ex)
    }
}

trait SlickActionComponent[P <: JdbcProfile]
    extends ActionComponent { profile: SlickProfile[P] =>
  /** Create the default IOActionContext for this repository. */
  def createPersistenceActionContext(cfg: Config): Context =
     EntityIOActionContext(config = cfg)
}

