package com.example.avianwatch.data

import java.util.UUID

class User {
    lateinit var uid: UUID
    lateinit var username: String
    var password: String = ""
    var email: String = ""
    var name: String = ""
}