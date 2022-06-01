package com.exception;

/**
 *
 * @author tingmailang
 */
public class DistributedProxyLockException extends RuntimeException {
  public DistributedProxyLockException() {
    super();
  }

  public DistributedProxyLockException(String message) {
    super("DistributedProxyLockException:" + message);
  }

  public DistributedProxyLockException(String message, Throwable cause) {
    super("DistributedProxyLockException:" + message, cause);
  }

  public DistributedProxyLockException(Throwable cause) {
    super(cause);
  }

}
