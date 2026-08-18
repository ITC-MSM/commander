package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.ChromaticSphere
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Chromatic Sphere (INV #299) — "{1}, {T}, Sacrifice this artifact: Add one mana of any color.
 * Draw a card."
 *
 * **Not a mana ability.** CR 605.1a gained "and its cost and effect don't move any card to or from
 * a library" on August 7, 2026, and the draw is exactly that. Nothing on the printed card changed
 * on that date, so the classification is invisible on inspection — which is why it is asserted
 * here rather than left to the snapshot and the linter.
 *
 * The reclassification is not just a flag: it moves the whole ability onto a different code path.
 * As a mana ability the colour was supplied at activation time as `ActivateAbility.manaColorChoice`
 * and `AddManaOfChoiceExecutor` never paused. Off the stack there is nobody to ask at activation,
 * so the executor raises a `ChooseColorDecision` in `DecisionPhase.RESOLUTION` and
 * `ColorChoiceContinuationResumer` finishes the composite — including the draw that sits *after*
 * the mana. That resume-through-a-rider path is what these tests pin.
 */
class ChromaticSphereScenarioTest : FunSpec({

    val abilityId = ChromaticSphere.activatedAbilities[0].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ChromaticSphere)
        return driver
    }

    test("the ability uses the stack — no mana and no card until it resolves") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val sphere = driver.putPermanentOnBattlefield(you, "Chromatic Sphere")
        driver.giveColorlessMana(you, 1)
        val handBefore = driver.getHand(you).size

        driver.submitSuccess(ActivateAbility(playerId = you, sourceId = sphere, abilityId = abilityId))

        // CR 605.1a — the draw keeps this off the mana-ability path, so the ability goes on the
        // stack and can be responded to. Neither half of the effect has happened yet.
        driver.assertStackSize(1)
        driver.state.getEntity(you)?.get<ManaPoolComponent>()?.total shouldBe 0
        driver.getHand(you).size shouldBe handBefore

        // Costs are still paid on activation: the artifact is already sacrificed.
        driver.assertInGraveyard(you, "Chromatic Sphere")
    }

    test("resolving asks for the colour and then draws — the rider after the mana still runs") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val sphere = driver.putPermanentOnBattlefield(you, "Chromatic Sphere")
        driver.giveColorlessMana(you, 1)
        val handBefore = driver.getHand(you).size

        driver.submitSuccess(ActivateAbility(playerId = you, sourceId = sphere, abilityId = abilityId))
        driver.bothPass()

        // Off the stack the colour can't ride on the activation, so the executor pauses for it.
        val decision = driver.pendingDecision
        decision shouldNotBe null
        decision.shouldBeInstanceOf<ChooseColorDecision>()

        driver.submitDecision(you, ColorChosenResponse(decision.id, Color.RED)).error shouldBe null

        driver.assertStackSize(0)
        val pool = driver.state.getEntity(you)?.get<ManaPoolComponent>()!!
        pool.red shouldBe 1
        pool.total shouldBe 1

        // The draw sits after the mana in the composite, so it only lands if the resumer carried
        // the rest of the effect through the pause.
        driver.getHand(you).size shouldBe handBefore + 1
    }

    test("a colour supplied at activation is ignored — the choice belongs to resolution") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val sphere = driver.putPermanentOnBattlefield(you, "Chromatic Sphere")
        driver.giveColorlessMana(you, 1)

        // `manaColorChoice` is the mana-ability path's way of settling the colour at activation, and
        // it is the field this card used to travel on. An ordinary activated ability makes that
        // choice as it resolves (CR 608.2), so the hint must not short-circuit anything.
        driver.submitSuccess(
            ActivateAbility(
                playerId = you,
                sourceId = sphere,
                abilityId = abilityId,
                manaColorChoice = Color.GREEN,
            )
        )
        driver.assertStackSize(1)
        driver.state.getEntity(you)?.get<ManaPoolComponent>()?.total shouldBe 0

        driver.bothPass()

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<ChooseColorDecision>()
        driver.submitDecision(you, ColorChosenResponse(decision.id, Color.BLUE)).error shouldBe null

        val pool = driver.state.getEntity(you)?.get<ManaPoolComponent>()!!
        pool.blue shouldBe 1
        pool.green shouldBe 0
        pool.total shouldBe 1
    }
})
