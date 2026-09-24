package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.ClingingDarkness
import com.wingedsheep.mtg.sets.definitions.rav.cards.FaithsFetters
import com.wingedsheep.mtg.sets.definitions.rav.cards.WarpWorld
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Warp World — {5}{R}{R}{R} Sorcery (Ravnica: City of Guilds #150)
 *
 * "Each player shuffles all permanents they own into their library, then reveals that many cards
 *  from the top of their library. Each player puts all artifact, creature, and land cards revealed
 *  this way onto the battlefield, then does the same for enchantment cards, then puts all cards
 *  revealed this way that weren't put onto the battlefield on the bottom of their library."
 *
 * The shuffle is random, so every test empties the libraries first: then "reveal that many" reads
 * exactly the shuffled-in permanents (plus any card the test planted), and the outcome is fixed.
 */
class WarpWorldScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + WarpWorld + ClingingDarkness + FaithsFetters)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        // Empty both libraries into exile so the reveals are deterministic.
        var s = d.state
        for (p in s.turnOrder) {
            for (id in s.getLibrary(p)) {
                s = s.removeFromZone(ZoneKey(p, Zone.LIBRARY), id).addToZone(ZoneKey(p, Zone.EXILE), id)
            }
        }
        d.replaceState(s)
        return d
    }

    fun GameTestDriver.names(ids: List<EntityId>) =
        ids.mapNotNull { state.getEntity(it)?.get<CardComponent>()?.name }

    fun GameTestDriver.attach(auraId: EntityId, hostId: EntityId) {
        var s = state.updateEntity(auraId) { it.with(AttachedToComponent(hostId)) }
        s = s.updateEntity(hostId) { it.with(AttachmentsComponent(listOf(auraId))) }
        replaceState(s)
    }

    /** Cast Warp World and answer every decision until the stack is empty. */
    fun GameTestDriver.castWarpWorld(
        auraHost: () -> EntityId? = { null },
        offeredHosts: MutableList<List<EntityId>> = mutableListOf()
    ) {
        val warp = putCardInHand(player1, "Warp World")
        giveMana(player1, Color.RED, 3)
        giveColorlessMana(player1, 5)
        castSpell(player1, warp).error shouldBe null
        var guard = 0
        while ((state.stack.isNotEmpty() || state.pendingDecision != null) && guard < 40) {
            val decision = state.pendingDecision
            when {
                decision is ChooseTargetsDecision -> {
                    offeredHosts += decision.legalTargets[0]!!
                    val host = auraHost() ?: decision.legalTargets[0]!!.first()
                    submitTargetSelection(decision.playerId, listOf(host)).error shouldBe null
                }
                decision != null -> autoResolveDecision()
                else -> bothPass()
            }
            guard++
        }
    }

    test("each player's permanents are shuffled away and that many cards come back") {
        val d = driver()
        val courser = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        val mountain = d.putLandOnBattlefield(d.player1, "Mountain")
        val lions = d.putCreatureOnBattlefield(d.player2, "Savannah Lions")

        d.castWarpWorld()

        withClue("the same cards return — they were the only cards in each library") {
            d.getPermanents(d.player1) shouldContainExactlyInAnyOrder listOf(courser, mountain)
            d.getPermanents(d.player2) shouldContainExactly listOf(lions)
        }
        withClue("every revealed card entered, leaving the library empty") {
            d.state.getLibrary(d.player1).size shouldBe 0
            d.state.getLibrary(d.player2).size shouldBe 0
        }
        d.getGraveyardCardNames(d.player1).contains("Warp World") shouldBe true
    }

    test("a token earns a card but never comes back, and unrevealed-permanent cards go to the bottom") {
        val d = driver()
        val mountain = d.putLandOnBattlefield(d.player1, "Mountain")
        val token = d.putCreatureOnBattlefield(d.player1, "Savannah Lions")
        d.addComponent(token, TokenComponent)
        val bolt = d.putCardOnTopOfLibrary(d.player1, "Lightning Bolt")

        d.castWarpWorld()

        withClue("two permanents owned → two cards revealed: the Mountain and the Bolt") {
            d.getPermanents(d.player1) shouldContainExactly listOf(mountain)
        }
        withClue("the token ceased to exist") {
            d.state.getBattlefield().contains(token) shouldBe false
            d.names(d.state.getLibrary(d.player1)).contains("Savannah Lions") shouldBe false
        }
        withClue("the revealed Bolt went to the bottom of the library") {
            d.state.getLibrary(d.player1) shouldContainExactly listOf(bolt)
        }
    }

    test("enchantments enter after the creatures, so an Aura can enchant a returned creature") {
        val d = driver()
        val courser = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        val aura = d.putPermanentOnBattlefield(d.player1, "Clinging Darkness")
        d.attach(aura, courser)

        d.castWarpWorld(auraHost = { courser })

        d.getPermanents(d.player1) shouldContainExactlyInAnyOrder listOf(courser, aura)
        withClue("the owner chose the returned Courser for the Aura") {
            d.state.getEntity(aura)?.get<AttachedToComponent>()?.targetId shouldBe courser
        }
    }

    test("an Aura with nothing to enchant stays revealed and goes to the bottom") {
        val d = driver()
        // A 3/3 survives Clinging Darkness's -4/-1 (a 2/1 host would die before the cast).
        val token = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        d.addComponent(token, TokenComponent)
        val aura = d.putPermanentOnBattlefield(d.player1, "Clinging Darkness")
        d.attach(aura, token)

        d.castWarpWorld()

        withClue("no creature exists anywhere, so the Aura can't enter") {
            d.getPermanents(d.player1) shouldBe emptyList()
            d.getPermanents(d.player2) shouldBe emptyList()
        }
        withClue("it is back in its owner's library, not the graveyard") {
            d.state.getLibrary(d.player1) shouldContainExactly listOf(aura)
            d.getGraveyardCardNames(d.player1).contains("Clinging Darkness") shouldBe false
        }
    }

    test("an Aura can't enchant an enchantment entering alongside it") {
        val d = driver()
        val mountain = d.putLandOnBattlefield(d.player1, "Mountain")
        val enchantment = d.putPermanentOnBattlefield(d.player1, "Test Enchantment")
        val fetters = d.putPermanentOnBattlefield(d.player1, "Faith's Fetters")
        d.attach(fetters, mountain)

        val offered = mutableListOf<List<EntityId>>()
        d.castWarpWorld(offeredHosts = offered)

        withClue("Faith's Fetters (enchant permanent) is offered only the Mountain, which entered first") {
            offered shouldContainExactly listOf(listOf(mountain))
        }
        d.getPermanents(d.player1) shouldContainExactlyInAnyOrder listOf(mountain, enchantment, fetters)
        d.state.getEntity(fetters)?.get<AttachedToComponent>()?.targetId shouldBe mountain
    }
})
