package com.wingedsheep.gameserver

import com.wingedsheep.gameserver.lobby.AiDeckSpec
import com.wingedsheep.gameserver.protocol.ClientMessage
import com.wingedsheep.gameserver.protocol.ServerMessage
import com.wingedsheep.sdk.core.DeckFormat
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class QuickGameLobbyCommanderAiTest : GameServerTestBase() {

    init {
        test("host can fill and reopen a normal 1v1 lobby with an AI opponent") {
            val client = createClient()
            client.connectAs("Flexible Host")
            client.send(ClientMessage.CreateQuickGameLobby())
            eventually(5.seconds) {
                client.messages.filterIsInstance<ServerMessage.QuickGameLobbyState>()
                    .lastOrNull()?.players?.size shouldBe 1
            }

            client.send(ClientMessage.AddQuickGameAi)
            eventually(5.seconds) {
                val state = client.messages.filterIsInstance<ServerMessage.QuickGameLobbyState>().lastOrNull()
                state?.vsAi shouldBe true
                state?.players?.count { it.isAi } shouldBe 1
            }

            client.send(ClientMessage.RemoveQuickGameAi)
            eventually(5.seconds) {
                val state = client.messages.filterIsInstance<ServerMessage.QuickGameLobbyState>().lastOrNull()
                state?.vsAi shouldBe false
                state?.players?.size shouldBe 1
            }
            client.allErrors() shouldBe emptyList()
        }

        test("AI quick lobby starts Commander with a host-supplied commander deck") {
            val client = createClient()
            client.connectAs("Commander Host")
            val library = mapOf("Plains" to 99)
            val commander = "Zetalpa, Primal Dawn"

            client.send(ClientMessage.CreateQuickGameLobby(vsAi = true, format = DeckFormat.COMMANDER))
            eventually(5.seconds) {
                client.messages.any { it is ServerMessage.QuickGameLobbyState } shouldBe true
            }

            client.send(
                ClientMessage.SetQuickGameAiDeck(
                    AiDeckSpec.Fixed(
                        deckList = library,
                        label = "Zetalpa",
                        commander = commander,
                    )
                )
            )
            client.send(
                ClientMessage.SubmitQuickGameLobbyDeck(
                    deckList = library,
                    commander = commander,
                )
            )
            client.send(ClientMessage.SetQuickGameLobbyReady(true))

            eventually(10.seconds) {
                client.messages.any { it is ServerMessage.GameCreated } shouldBe true
            }
            client.allErrors() shouldBe emptyList()
        }

        test("commander-shaped AI deck is rejected without a designated commander") {
            val client = createClient()
            client.connectAs("Commander Host")
            client.send(ClientMessage.CreateQuickGameLobby(vsAi = true, format = DeckFormat.COMMANDER))
            eventually(5.seconds) {
                client.messages.any { it is ServerMessage.QuickGameLobbyState } shouldBe true
            }

            client.send(
                ClientMessage.SetQuickGameAiDeck(
                    AiDeckSpec.Fixed(deckList = mapOf("Plains" to 100))
                )
            )

            eventually(5.seconds) {
                client.latestError()?.message?.contains("commander", ignoreCase = true) shouldBe true
            }
        }
    }
}
