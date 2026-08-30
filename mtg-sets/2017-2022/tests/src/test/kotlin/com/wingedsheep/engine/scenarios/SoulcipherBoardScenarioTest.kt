package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.vow.cards.SoulcipherBoard
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Soulcipher Board // Cipherbound Spirit (VOW) — {1}{U} Artifact.
 *
 * "This artifact enters with three omen counters on it."
 * "{1}{U}, {T}: Look at the top two cards of your library. Put one of them into your graveyard."
 * "Whenever a creature card is put into your graveyard from anywhere, remove an omen counter from
 *  this artifact. Then if it has no omen counters on it, transform it."
 *
 * Covers the new [com.wingedsheep.sdk.core.Counters.OMEN] countdown counter, the per-card (not
 * batching) graveyard trigger, and the transform once the last counter is gone — plus the two
 * ways the countdown must *not* run: a dying creature token isn't a card, and a second trigger
 * resolving after the board has already turned over must not turn it back (CR 701.28f).
 */
class SoulcipherBoardScenarioTest : FunSpec({

    fun omenCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.OMEN) ?: 0

    /** A real 1/1 creature token — no card definition behind it, so it is not a creature card. */
    fun GameTestDriver.createMouseToken(playerId: EntityId): EntityId {
        val tokenId = EntityId.generate()
        val container = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "token:Mouse",
                name = "Mouse Token",
                manaCost = ManaCost.ZERO,
                typeLine = TypeLine.parse("Creature - Mouse"),
                baseStats = CreatureStats(1, 1),
                colors = setOf(Color.WHITE),
                ownerId = playerId,
            ),
            TokenComponent,
            ControllerComponent(playerId),
            SummoningSicknessComponent,
        )
        replaceState(
            state.withEntity(tokenId, container)
                .addToZone(ZoneKey(playerId, Zone.BATTLEFIELD), tokenId)
        )
        return tokenId
    }

    /** Cast Soulcipher Board for real so its enters-with-counters replacement runs. */
    fun boardOnBattlefield(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + SoulcipherBoard)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val card = driver.putCardInHand(me, "Soulcipher Board")
        driver.giveColorlessMana(me, 1)
        driver.giveMana(me, Color.BLUE, 1)
        driver.castSpell(me, card).isSuccess shouldBe true
        driver.bothPass()

        return driver to driver.findPermanent(me, "Soulcipher Board")!!
    }

    test("enters with three omen counters") {
        val (driver, board) = boardOnBattlefield()
        omenCounters(driver, board) shouldBe 3
    }

    test("the tap ability puts one of the top two cards into the graveyard and leaves the other on the library") {
        val (driver, board) = boardOnBattlefield()
        val me = driver.activePlayer!!

        // Stack the library so the top two are known: Grizzly Bears on top, Lightning Bolt beneath.
        val bolt = driver.putCardOnTopOfLibrary(me, "Lightning Bolt")
        val bears = driver.putCardOnTopOfLibrary(me, "Grizzly Bears")

        driver.giveColorlessMana(me, 1)
        driver.giveMana(me, Color.BLUE, 1)
        val activation = driver.legalActions(me).first { it.description.contains("Look at the top two") }
        driver.submitSuccess(activation.action)
        driver.bothPass()

        // Choose the Bears to go to the graveyard.
        driver.submitCardSelection(me, listOf(bears)).isSuccess shouldBe true
        driver.bothPass()

        withClue("the chosen card is put into the graveyard") {
            driver.getGraveyard(me).contains(bears) shouldBe true
        }
        withClue("the other card stays in the library, on top") {
            driver.getGraveyard(me).contains(bolt) shouldBe false
        }
        withClue("a creature card hit the graveyard, so one omen counter came off") {
            omenCounters(driver, board) shouldBe 2
        }
    }

    test("three creature cards in the graveyard transform it into Cipherbound Spirit") {
        val (driver, board) = boardOnBattlefield()
        val me = driver.activePlayer!!

        repeat(3) { i ->
            val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
            val bolt = driver.putCardInHand(me, "Lightning Bolt")
            driver.giveMana(me, Color.RED, 1)
            driver.castSpell(me, bolt, listOf(bears)).isSuccess shouldBe true
            driver.bothPass() // resolve the Bolt; the Bears dies
            driver.bothPass() // resolve the omen-counter trigger

            if (i < 2) {
                withClue("after ${i + 1} creature death(s) the board still has counters") {
                    omenCounters(driver, board) shouldBe 2 - i
                    driver.findPermanent(me, "Soulcipher Board") shouldNotBe null
                }
            }
        }

        withClue("the last omen counter coming off transforms the artifact") {
            driver.findPermanent(me, "Cipherbound Spirit") shouldNotBe null
            driver.getCardName(board) shouldBe "Cipherbound Spirit"
        }
    }

    test("a noncreature card reaching the graveyard removes no counter") {
        val (driver, board) = boardOnBattlefield()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        // The Bolt itself is an instant card put into my graveyard as it resolves.
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 1)
        driver.castSpell(me, bolt, listOf(opp)).isSuccess shouldBe true
        driver.bothPass()

        driver.getGraveyard(me).contains(bolt) shouldBe true
        withClue("only creature cards count down the omen counters") {
            omenCounters(driver, board) shouldBe 3
        }
    }
    /**
     * Two creature cards reaching the graveyard together put *two* triggers on the stack. With one
     * omen counter left, the first removes it and transforms the board; the second then finds no
     * counter to remove and a counter count of zero, so its "then if it has no omen counters"
     * check passes and it reaches the transform instruction too.
     *
     * CR 701.28f: that instruction is ignored, because the permanent has already transformed since
     * that ability was put onto the stack. Before the rule was enforced the board flipped straight
     * back to its artifact face and Cipherbound Spirit vanished from the battlefield.
     */
    test("a second trigger resolving after the transform does not turn the board back over") {
        val (driver, board) = boardOnBattlefield()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        driver.addComponent(board, CountersComponent(mapOf(CounterType.OMEN to 1)))

        // Two of my creatures trade in combat, so both hit my graveyard at the same time.
        val attackerA = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val attackerB = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        driver.removeSummoningSickness(attackerA)
        driver.removeSummoningSickness(attackerB)
        val blockerA = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val blockerB = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(attackerA, attackerB), opp).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(
            opp,
            mapOf(blockerA to listOf(attackerA), blockerB to listOf(attackerB))
        ).error shouldBe null
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        withClue("both creature cards did reach my graveyard, so both triggers fired") {
            driver.getGraveyardCardNames(me).count { it == "Grizzly Bears" } shouldBe 2
        }
        withClue("the transformed permanent stays on the battlefield as Cipherbound Spirit") {
            driver.getCardName(board) shouldBe "Cipherbound Spirit"
            driver.findPermanent(me, "Cipherbound Spirit") shouldNotBe null
            driver.findPermanent(me, "Soulcipher Board") shouldBe null
        }
    }

    /**
     * "Whenever a creature **card** is put into your graveyard" — a token creature dying is not a
     * card and never counts the board down (the card's second Gatherer ruling).
     */
    test("a dying creature token removes no counter") {
        val (driver, board) = boardOnBattlefield()
        val me = driver.activePlayer!!

        val token = driver.createMouseToken(me)
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 1)
        driver.castSpell(me, bolt, listOf(token)).isSuccess shouldBe true
        driver.bothPass()

        withClue("the token died") {
            driver.getPermanents(me).contains(token) shouldBe false
        }
        withClue("a token is not a creature card, so the countdown does not run") {
            omenCounters(driver, board) shouldBe 3
        }
    }
})
