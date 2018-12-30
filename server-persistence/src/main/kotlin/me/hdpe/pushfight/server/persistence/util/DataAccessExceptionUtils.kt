package me.hdpe.pushfight.server.persistence.util

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException

fun isConstraintViolation(exception: DataAccessException, constraintName: String): Boolean {
    // ConstraintViolationException.constraintName is full of cruft on h2
    return exception is DataIntegrityViolationException
            && exception.cause is ConstraintViolationException
            && (exception.cause as ConstraintViolationException).constraintName?.toLowerCase()?.contains(
            constraintName.toLowerCase()) ?: false
}