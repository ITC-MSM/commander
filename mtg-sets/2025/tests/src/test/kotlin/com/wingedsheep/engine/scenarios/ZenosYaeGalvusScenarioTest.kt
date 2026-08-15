package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.GameInitializer
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.battlefield.chosenOpponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.ZenosYaeGalvus
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Zenos yae Galvus // Shinryu, Transcendent Rival (FIN #127).
 *
 * Proves the card's assembly and the two engine primitives it introduced:
 *  - The "My First Friend" ETB gives every creature except Zenos and the chosen creature -2/-2, and
 *    still resolves the debuff when there is no opponent creature to choose (the printed ruling).
 *  - "When the chosen creature leaves the battlefield, transform" is a persistent
 *    ([DelayedTriggerExpiry.Never]) reflexive delayed trigger — it survives an end-of-turn cleanup
 *    and fires only for the watched creature, then flips Zenos to its Shinryu back face.
 *  - Shinryu's `Triggers.AnyPlayerLosesGame` + `Conditions.TriggeringPlayerIs(Player.ChosenOpponent)`
 *    + `Effects.WinGame()` make you win when your chosen opponent loses — proven discriminatingly in
 *    a 3-player pod (an unchosen opponent's loss does not win it for you).
 */
class ZenosYaeGalvusScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ZenosYaeGalvus))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        return driver
    }

    // Player 1 may not be active at game start (random turn order) — advance until it is.
    fun GameTestDriver.advanceToPlayer1(targetStep: Step) {
        passPriorityUntil(targetStep)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(targetStep)
            safety++
        }
    }

    fun faceName(driver: GameTestDriver, id: EntityId): String =
        driver.state.getEntity(id)!!.get<CardComponent>()!!.name

    /** Walk the stack + any decisions to rest, choosing [chosen] (or nothing) for Zenos's ETB. */
    fun resolveWithChoice(driver: GameTestDriver, chosen: EntityId?) {
        var guard = 0
        while (guard++ < 40 && (driver.state.stack.isNotEmpty() || driver.isPaused)) {
            val decision = driver.pendingDecision
            if (driver.isPaused && decision is ChooseTargetsDecision) {
                driver.submitTargetSelection(
                    decision.playerId,
                    if (chosen != null) listOf(chosen) else emptyList()
                )
            } else if (driver.isPaused) {
                driver.autoResolveDecision()
            } else {
                driver.bothPass()
            }
        }
    }

    /** Cast Zenos from [me]'s hand (auto-paying) and resolve its ETB, choosing [chosen]. */
    fun castZenos(driver: GameTestDriver, me: EntityId, chosen: EntityId?): EntityId {
        val card = driver.putCardInHand(me, "Zenos yae Galvus")
        driver.giveMana(me, Color.BLACK, 5) // {3}{B}{B}
        driver.castSpell(me, card).isSuccess shouldBe true
        resolveWithChoice(driver, chosen)
        return driver.findPermanent(me, "Zenos yae Galvus")
            ?: driver.findPermanent(me, "Shinryu, Transcendent Rival")!!
    }

    /** Kill [victim] with a Lightning Bolt from [caster] via the real event pipeline, then settle. */
    fun killWithBolt(driver: GameTestDriver, caster: EntityId, victim: EntityId) {
        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, bolt, targets = listOf(victim)).isSuccess shouldBe true
        var guard = 0
        while (guard++ < 40 && (driver.state.stack.isNotEmpty() || driver.isPaused)) {
            if (driver.isPaused) driver.autoResolveDecision() else driver.bothPass()
        }
    }

    test("My First Friend gives -2/-2 to every creature except Zenos and the chosen creature") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val myBear = driver.putCreatureOnBattlefield(me, "Centaur Courser")   // 3/3 -> 1/1
        val chosen = driver.putCreatureOnBattlefield(opp, "Centaur Courser")  // 3/3, excluded
        val other = driver.putCreatureOnBattlefield(opp, "Centaur Courser")   // 3/3 -> 1/1

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val zenos = castZenos(driver, me, chosen)

        val projected = projector.project(driver.state)
        // Zenos itself is excluded (excludeSelf).
        projected.getPower(zenos) shouldBe 4
        projected.getToughness(zenos) shouldBe 4
        // The chosen creature is excluded (excludeTarget).
        projected.getPower(chosen) shouldBe 3
        projected.getToughness(chosen) shouldBe 3
        // Every other creature — mine and the opponent's — took -2/-2.
        projected.getPower(myBear) shouldBe 1
        projected.getToughness(myBear) shouldBe 1
        projected.getPower(other) shouldBe 1
        projected.getToughness(other) shouldBe 1
    }

    test("with no creature to choose, the other creatures still get -2/-2 (printed ruling)") {
        val driver = createDriver()
        val me = driver.player1

        val myBear = driver.putCreatureOnBattlefield(me, "Centaur Courser") // 3/3 -> 1/1
        // The opponent controls no creatures — nothing to choose.

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val zenos = castZenos(driver, me, null)

        val projected = projector.project(driver.state)
        projected.getPower(zenos) shouldBe 4
        projected.getPower(myBear) shouldBe 1
        projected.getToughness(myBear) shouldBe 1
    }

    test("transforms into Shinryu when the chosen creature leaves — surviving an end-of-turn cleanup") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val chosen = driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val zenos = castZenos(driver, me, chosen)
        faceName(driver, zenos) shouldBe "Zenos yae Galvus"

        // Cross two turn boundaries so the reflexive delayed trigger survives cleanup (Never expiry),
        // and priority returns to me at a main phase to cast the removal.
        driver.passPriorityUntil(Step.END)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.END)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        faceName(driver, zenos) shouldBe "Zenos yae Galvus"

        // The chosen creature leaves → transform Zenos. Two full turns have passed, so it is my
        // turn again and I have priority to cast the removal.
        driver.activePlayer shouldBe me
        killWithBolt(driver, me, chosen)
        faceName(driver, zenos) shouldBe "Shinryu, Transcendent Rival"
        // Transforming into Shinryu chose the sole opponent.
        driver.state.getEntity(zenos)!!.chosenOpponent() shouldBe opp
    }

    test("a different creature leaving does not transform Zenos") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val chosen = driver.putCreatureOnBattlefield(opp, "Centaur Courser")
        val other = driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val zenos = castZenos(driver, me, chosen)

        // Kill the creature that was NOT chosen — the watch is scoped to the chosen creature only.
        killWithBolt(driver, me, other)
        faceName(driver, zenos) shouldBe "Zenos yae Galvus"
    }

    test("Shinryu: when the chosen player loses the game, you win — even with another opponent alive") {
        // A single-faced stand-in carrying Shinryu's win trigger, so a 3-player pod can be built
        // directly (the transform + ChooseOpponent path is covered by the 2-player tests above).
        val watcher = card("Chosen Doom Watcher") {
            manaCost = "{B}"
            colorIdentity = "B"
            typeLine = "Creature — Human"
            power = 1
            toughness = 1
            triggeredAbility {
                trigger = Triggers.AnyPlayerLosesGame
                triggerCondition = Conditions.TriggeringPlayerIs(Player.ChosenOpponent)
                effect = Effects.WinGame()
            }
        }

        fun runPod(chosenOpponentIndex: Int): Pair<GameTestDriver, List<EntityId>> {
            val registry = CardRegistry()
            (TestCards.all + watcher).forEach { registry.register(it) }
            val deck = Deck(cards = List(40) { "Centaur Courser" })
            val init = GameInitializer(registry).initializeGame(
                GameConfig(
                    players = (1..3).map { PlayerConfig("Player $it", deck, 20) },
                    skipMulligans = true,
                    startingPlayerIndex = 0
                )
            )
            val driver = GameTestDriver()
            driver.registerCards(TestCards.all + watcher)
            driver.replaceState(init.state)

            val p1 = init.playerIds[0]
            val chronicler = driver.putCreatureOnBattlefield(p1, "Chosen Doom Watcher")
            driver.addComponent(
                chronicler,
                CastChoicesComponent(
                    chosen = mapOf(
                        ChoiceSlot.OPPONENT to ChoiceValue.EntityChoice(init.playerIds[chosenOpponentIndex])
                    )
                )
            )
            // The fresh state starts in the untap step; advance to a main phase so priority passing
            // runs state-based actions normally.
            var g = 0
            while (g++ < 40 && driver.state.step != Step.PRECOMBAT_MAIN) {
                val pid = driver.state.priorityPlayerId ?: break
                if (!driver.submit(PassPriority(pid)).isSuccess) break
            }
            return driver to init.playerIds
        }

        // Pass priority (for whoever holds it) up to a bounded cap. Passing priority runs
        // state-based actions (marking the 0-life player lost → PlayerLostEvent) and detects the
        // resulting triggers, then lets the "you win the game" trigger be placed and resolve. Stops
        // as soon as the game ends, staying early in the turn (never reaching a combat declaration).
        fun settle(driver: GameTestDriver, passes: Int) {
            var guard = 0
            while (guard++ < passes && !driver.state.gameOver) {
                if (driver.isPaused) {
                    driver.autoResolveDecision()
                } else {
                    val pid = driver.state.priorityPlayerId ?: break
                    driver.submit(PassPriority(pid))
                }
            }
        }

        // Deal lethal to [victim] with a Lightning Bolt cast by whoever holds priority, then settle.
        // Resolving the bolt runs the post-resolution state-based-action sweep (marking the 0-life
        // player lost → PlayerLostEvent), which the priority pipeline feeds to trigger detection.
        fun boltToDeath(driver: GameTestDriver, victim: EntityId) {
            driver.replaceState(driver.state.withLifeTotal(victim, 2))
            val caster = driver.state.priorityPlayerId!!
            val bolt = driver.putCardInHand(caster, "Lightning Bolt")
            driver.giveMana(caster, Color.RED, 1)
            driver.castSpell(caster, bolt, targets = listOf(victim)).isSuccess shouldBe true
            settle(driver, passes = 12)
        }

        // Chosen opponent = Player 2. Player 2 loses → Shinryu's controller (Player 1) wins, even
        // though Player 3 is still in the game.
        val (winDriver, winPlayers) = runPod(chosenOpponentIndex = 1)
        boltToDeath(winDriver, winPlayers[1])
        winDriver.state.gameOver shouldBe true
        winDriver.state.winnerId shouldBe winPlayers[0]

        // Control: chosen opponent = Player 3, but Player 2 loses. The trigger does not fire, so the
        // game continues with Players 1 and 3 — no free win.
        val (contDriver, contPlayers) = runPod(chosenOpponentIndex = 2)
        boltToDeath(contDriver, contPlayers[1])
        contDriver.state.gameOver shouldBe false
    }
})
