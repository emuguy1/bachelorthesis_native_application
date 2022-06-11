package de.num42.sharing.data

import de.num42.sharing.graphql.type.DateTime

class Conversation {
    lateinit var lastMessage: String
    lateinit var lastMessageAt: String
    lateinit var creator: String
    lateinit var conversationId: String
}