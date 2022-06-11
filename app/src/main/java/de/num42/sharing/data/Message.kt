package de.num42.sharing.data

class Message {
    lateinit var message: String
    lateinit var messagePartner: String
    lateinit var sendtime: String
    lateinit var sender: String
    var sendByMe: Boolean = false
}