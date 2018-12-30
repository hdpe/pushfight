package me.hdpe.pushfight.server.persistence.account

class NoSuchAccountException(val id: String) : RuntimeException()
