package de.num42.sharing.data

import de.num42.sharing.graphql.type.DateTime

class Conversation {
    lateinit var lastMessage: String
    lateinit var lastMessageAt: DateTime
    lateinit var creator: String
}