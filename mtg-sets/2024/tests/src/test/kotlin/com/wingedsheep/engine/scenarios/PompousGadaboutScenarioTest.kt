package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.MorphDataComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.PompousGadabout
import com.wingedsheep.mtg.sets.definitions.scg.cards.ZombieCutthroat
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Pompous Gadabout — "During your turn, this creature has hexproof. This creature can't be blocked
 * by creatures that don't have a name."
 *
 * The second line is the one worth pinning down, because "creatures that don't have a name" has no
 * literal spelling in the SDK: it is modeled as the face-down set (CR 708.2, and the card's own
 * ruling — *"Face-down creatures don't have names unless an effect says otherwise"*). A filter that
 * silently matched *every* creature would make the Gadabout unblockable and every test that only
 * checked the face-down case would still pass, so both directions are asserted here: the face-down
 * blocker is rejected and the face-up one is accepted, in the same combat.
 *
 * The hexproof half is a conditional static, so the interesting assertion is the turn it is *not*
 * active — a `GrantKeyword` wired without its condition looks identical on your own turn.
 */
class PompousGadaboutScenarioTest : FunSpec({

    val allCards = TestCards.all + listOf(PompousGadabout, ZombieCutthroat)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(allCards)
        return driver
    }

    /** A face-down (hence nameless) creature on [playerId]'s battlefield, ready to block. */
    fun GameTestDriver.putFaceDownCreature(playerId: EntityId, cardName: String): EntityId {
        val creatureId = putCreatureOnBattlefield(playerId, cardName)
        val cardDef = allCards.first { it.name == cardName }
        val morphAbility = cardDef.keywordAbilities.filterIsInstance<KeywordAbility.Morph>().firstOrNull()
        replaceState(
            state.updateEntity(creatureId) { container ->
                var c = container.with(FaceDownComponent)
                if (morphAbility != null) {
                    c = c.with(MorphDataComponent(morphAbility.morphCost, cardDef.name))
                }
                c
            }
        )
        removeSummoningSickness(creatureId)
        return creatureId
    }

    test("a face-down creature can't block it, but a face-up one can") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40, "Swamp" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gadabout = driver.putCreatureOnBattlefield(active, "Pompous Gadabout")
        driver.removeSummoningSickness(gadabout)

        val nameless = driver.putFaceDownCreature(opponent, "Zombie Cutthroat")
        val named = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(active, listOf(gadabout), opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        withClue("a face-down creature has no name, so it can't block the Gadabout") {
            driver.declareBlockers(opponent, mapOf(nameless to listOf(gadabout))).error shouldNotBe null
        }
        withClue("a face-up creature has a name and blocks normally — the filter isn't 'any creature'") {
            driver.declareBlockers(opponent, mapOf(named to listOf(gadabout))).error shouldBe null
        }
    }

    test("hexproof is present on your turn and gone on the opponent's") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gadabout = driver.putCreatureOnBattlefield(active, "Pompous Gadabout")

        withClue("during its controller's turn the conditional static grants hexproof") {
            driver.state.projectedState.hasKeyword(gadabout, Keyword.HEXPROOF) shouldBe true
        }

        // Roll into the next turn — the next upkeep belongs to the opponent.
        driver.passPriorityUntil(Step.UPKEEP)
        driver.activePlayer shouldNotBe active

        withClue("on anyone else's turn the condition is false and the keyword is simply absent") {
            driver.state.projectedState.hasKeyword(gadabout, Keyword.HEXPROOF) shouldBe false
        }
    }
})
