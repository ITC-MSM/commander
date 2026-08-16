package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SpellFizzledEvent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ecl.cards.Personify
import com.wingedsheep.mtg.sets.definitions.ltr.cards.CastIntoTheFire
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * PARTIAL-TARGET-MODAL-001 — CR 608.2b target recheck at resolution.
 *
 * The oracle-approved interaction is Cast into the Fire's first mode ("up to two
 * target creatures") with Personify used as the existing blink fixture. Personify
 * is deliberately used instead of a test-only Cloudshift: its exile-and-return
 * instruction makes the selected permanent a new object while retaining an
 * observable battlefield object for the assertions below.
 */
class CastIntoTheFirePartialFizzleScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().also { driver ->
        driver.registerCards(TestCards.all)
        driver.registerCard(CastIntoTheFire)
        driver.registerCard(Personify)
    }

    fun GameTestDriver.damageOn(id: EntityId): Int =
        state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    fun GameTestDriver.castFire(
        caster: EntityId,
        card: EntityId,
        first: EntityId,
        second: EntityId,
    ) = submit(
        CastSpell(
            playerId = caster,
            cardId = card,
            targets = listOf(ChosenTarget.Permanent(first), ChosenTarget.Permanent(second)),
            chosenModes = listOf(0),
            modeTargetsOrdered = listOf(
                listOf(ChosenTarget.Permanent(first), ChosenTarget.Permanent(second))
            ),
        )
    )

    test("one blinked target is ignored while Cast into the Fire damages the remaining original target") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Plains" to 20))
        val caster = d.activePlayer!!
        val defender = d.getOpponent(caster)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val blinked = d.putCreatureOnBattlefield(defender, "Grizzly Bears")
        val remaining = d.putCreatureOnBattlefield(defender, "Centaur Courser")
        d.giveMana(caster, Color.RED, 1)
        d.giveColorlessMana(caster, 1)
        d.giveMana(defender, Color.WHITE, 1)
        d.giveColorlessMana(defender, 1)
        val fire = d.putCardInHand(caster, "Cast into the Fire")
        val blink = d.putCardInHand(defender, "Personify")

        d.castFire(caster, fire, blinked, remaining).error shouldBe null

        // The caster retains priority after casting, then passes to the defender's response window.
        d.passPriority(caster).error shouldBe null
        d.castSpell(defender, blink, listOf(blinked)).error shouldBe null
        d.bothPass() // Personify resolves: blinked leaves and returns as a new object.

        // The engine keeps the entity ID but changes its zone-entry identity; target
        // validation must therefore reject the old target even though this helper still
        // addresses the returned permanent by the same ID.
        d.damageOn(blinked) shouldBe 0

        d.bothPass() // Cast into the Fire resolves with exactly one legal target remaining.

        d.damageOn(remaining) shouldBe 1
        d.damageOn(blinked) shouldBe 0
    }

    test("Cast into the Fire fizzles when both of its original targets are blinked") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Plains" to 20))
        val caster = d.activePlayer!!
        val defender = d.getOpponent(caster)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val first = d.putCreatureOnBattlefield(defender, "Grizzly Bears")
        val second = d.putCreatureOnBattlefield(defender, "Centaur Courser")
        d.giveMana(caster, Color.RED, 1)
        d.giveColorlessMana(caster, 1)
        d.giveMana(defender, Color.WHITE, 2)
        d.giveColorlessMana(defender, 2)
        val fire = d.putCardInHand(caster, "Cast into the Fire")
        val firstBlink = d.putCardInHand(defender, "Personify")
        val secondBlink = d.putCardInHand(defender, "Personify")

        d.castFire(caster, fire, first, second).error shouldBe null
        d.passPriority(caster).error shouldBe null
        d.castSpell(defender, firstBlink, listOf(first)).error shouldBe null
        // The defender retains priority after responding, so it may add the second blink.
        d.castSpell(defender, secondBlink, listOf(second)).error shouldBe null

        d.bothPass() // second Personify
        d.bothPass() // first Personify
        val eventStart = d.events.size
        d.bothPass() // Cast into the Fire: every original target is now illegal.

        d.damageOn(first) shouldBe 0
        d.damageOn(second) shouldBe 0
        d.events.drop(eventStart).filterIsInstance<SpellFizzledEvent>().size shouldBe 1
    }

    test("the creature-damage mode rejects a player supplied as a target without mutating game state") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 20))
        val caster = d.activePlayer!!
        val opponent = d.getOpponent(caster)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.giveMana(caster, Color.RED, 1)
        d.giveColorlessMana(caster, 1)
        val fire = d.putCardInHand(caster, "Cast into the Fire")
        val eventsBefore = d.events.size

        val result = d.submit(
            CastSpell(
                playerId = caster,
                cardId = fire,
                targets = listOf(ChosenTarget.Player(opponent)),
                chosenModes = listOf(0),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Player(opponent))),
            )
        )

        result.isSuccess shouldBe false
        result.error.shouldNotBeNull()
        d.state.getHand(caster).contains(fire) shouldBe true
        d.state.stack.isEmpty() shouldBe true
        d.events.size shouldBe eventsBefore
    }
})
