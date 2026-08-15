package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SummonBrynhildr
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Summon: Brynhildr — {1}{R} Enchantment Creature — Saga Knight, 2/1 (FIN).
 *
 *   I  — Chain — Exile the top card of your library. During any turn you put a lore counter on this
 *        Saga, you may play that card.
 *   II, III — Gestalt Mode — When you next cast a creature spell this turn, it gains haste until end
 *        of turn.
 *
 * Chapter I exercises the impulse-exile + conditional play permission (Possibility Technician /
 * Lightning, Security Sergeant shape). Chapters II/III exercise the "next creature spell you cast
 * gains haste" delayed trigger — a keyword granted to a creature spell while it is on the stack,
 * which must carry onto the permanent when it resolves.
 */
class SummonBrynhildrScenarioTest : FunSpec({

    val projector = StateProjector()

    fun GameTestDriver.loreCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LORE) ?: 0

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SummonBrynhildr))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.resolveAll() {
        var guard = 0
        while ((state.stack.isNotEmpty() || state.pendingDecision != null) && guard++ < 50) {
            val pd = state.pendingDecision
            if (pd != null) autoResolveDecision() else bothPass()
        }
    }

    fun GameTestDriver.castBrynhildr(me: EntityId): EntityId {
        val spell = putCardInHand(me, "Summon: Brynhildr")
        giveColorlessMana(me, 1)
        giveMana(me, Color.RED, 1)
        castSpell(me, spell)
        return spell
    }

    /** Advance turns (auto-resolving decisions / passing priority) until [predicate] holds. */
    fun GameTestDriver.advanceUntil(maxSteps: Int = 2000, predicate: GameTestDriver.() -> Boolean) {
        var guard = 0
        while (guard++ < maxSteps && !predicate()) {
            val pd = state.pendingDecision
            when {
                pd != null -> autoResolveDecision()
                state.priorityPlayerId != null -> {
                    autoSubmitCombatDeclarationIfNeeded()
                    passPriority(state.priorityPlayerId!!)
                }
            }
        }
    }

    test("chapter I (Chain) — exiles the top card and lets you play it on your turn") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        // A known nonland top card so the exiled card is deterministic and castable.
        driver.putCardOnTopOfLibrary(me, "Centaur Courser")

        driver.castBrynhildr(me)
        driver.resolveAll()

        // Chapter I exiled the top card of the library.
        driver.getExileCardNames(me) shouldContain "Centaur Courser"

        // On your turn, while you control the Saga, the exiled card is playable from exile.
        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, Color.GREEN, 1)
        val canPlayExiled = driver.legalActions(me).any { la ->
            val action = la.action
            action is CastSpell &&
                driver.state.getEntity(action.cardId)?.get<CardComponent>()?.name == "Centaur Courser"
        }
        canPlayExiled shouldBe true
    }

    test("chapter II (Gestalt Mode) — your next creature spell enters with haste") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.castBrynhildr(me)
        driver.resolveAll()

        // Advance until the Saga has its second lore counter (chapter II installs the delayed
        // trigger), then finish resolving it on this — my — turn.
        driver.advanceUntil {
            val s = findPermanent(me, "Summon: Brynhildr") ?: return@advanceUntil true
            loreCounters(s) >= 2
        }
        driver.findPermanent(me, "Summon: Brynhildr") shouldNotBe null
        driver.resolveAll()

        // Cast a creature with no innate haste; the delayed trigger grants it haste as it resolves.
        val courser = driver.putCardInHand(me, "Centaur Courser")
        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, Color.GREEN, 1)
        driver.castSpell(me, courser)
        driver.resolveAll()

        val perm = driver.findPermanent(me, "Centaur Courser")!!
        projector.project(driver.state).hasKeyword(perm, Keyword.HASTE) shouldBe true
    }
})
