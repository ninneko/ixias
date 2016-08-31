/*
 *  This file is part of the nextbeat services.
 *
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package ixias.mail

import scala.util.Try
import ixias.persistence.model.Configuration

trait EmailConfig {

  // --[ Properties ]-----------------------------------------------------------
  protected val CF_MAIL_CHARSET        = "mail.charset"
  protected val CF_MAIL_BOUNCE_ADDRESS = "mail.bounce_address"
  protected val CF_SMTP_HOST           = "mail.smtp.host"
  protected val CF_SMTP_PORT           = "mail.smtp.port"
  protected val CF_SMTP_SSL            = "mail.smtp.ssl"
  protected val CF_SMTP_TLS            = "mail.smtp.tls"
  protected val CF_SMTP_USER           = "mail.smtp.user"
  protected val CF_SMTP_PASSWORD       = "mail.smtp.password"
  protected val CF_SMTP_TIMEOUT        = "mail.smtp.timeout"
  protected val CF_SMTP_CON_TIMEOUT    = "mail.smtp.connection_timeout"
  protected val CF_TWILLIO_SID         = "mail.twillio.sid"
  protected val CF_TWILLIO_TOKEN       = "mail.twillio.token"
  protected val CF_TWILLIO_FROM        = "mail.twillio.from"

  // --[ Properties ]-----------------------------------------------------------
  protected val config = Configuration()

  // --[ Methods ]--------------------------------------------------------------
  /**
   * Gets a E-mail's charset.
   */
  def getCharset(): String =
    config.getString(CF_MAIL_CHARSET).getOrElse("utf-8")

  /**
   * Gets a bounce mail address.
   */
  def getBounceAddress(): Option[String] =
    config.getString(CF_MAIL_BOUNCE_ADDRESS)

  // --[ Methods ]--------------------------------------------------------------
  /**
   * SMTP : Gets the host name of the SMTP server
   */
  def getSmtpHost(): Try[String] =
    Try(config.getString(CF_SMTP_HOST).get)

  /**
   * SMTP : Gets the listening port of the SMTP server.
   */
  def getSmtpPort(): Int =
    config.getInt(CF_SMTP_PORT).getOrElse(25)

  /**
   * SMTP : Returns whether SSL/TLS encryption for the transport is currently enabled (SMTPS/POPS).
   */
  def getSmtpSSL(): Boolean =
    config.getBoolean(CF_SMTP_SSL).getOrElse(false)

  /**
   * SMTP : Gets whether the client is configured to try to enable STARTTLS.
   */
  def getSmtpTLS(): Boolean =
    config.getBoolean(CF_SMTP_TLS).getOrElse(false)

  /**
   * SMTP : Gets the user's name if authentication is needed.
   */
  def getSmtpUser(): Option[String] =
    config.getString(CF_SMTP_USER)

  /**
   * SMTP : Gets the user's passowrd if authentication is needed.
   */
  def getSmtpPassword(): Option[String] =
    config.getString(CF_SMTP_PASSWORD)

  /**
   * SMTP : Gets the socket I/O timeout value in milliseconds.
   */
  def getSmtpTimeout(): Option[Long] =
    config.getMilliseconds(CF_SMTP_TIMEOUT)

  /**
   * SMTP : Gets the socket connection timeout value in milliseconds.
   */
  def getSmtpConnectionTimeout(): Option[Long] =
    config.getMilliseconds(CF_SMTP_CON_TIMEOUT)

  // --[ Methods ]--------------------------------------------------------------
  /**
   * Twillio : Gets the service sid.
   */
  def getTwillioSid(): Try[String] =
    Try(config.getString(CF_TWILLIO_SID).get)

  /**
   * Twillio : Gets the service auth-token.
   */
  def getTwillioAuthToken(): Try[String] =
    Try(config.getString(CF_TWILLIO_TOKEN).get)

  /**
   * Twillio : Gets a sender's phone number.
   */
  def getTwillioFrom(): Try[String] =
    Try(config.getString(CF_TWILLIO_FROM).get)
}
