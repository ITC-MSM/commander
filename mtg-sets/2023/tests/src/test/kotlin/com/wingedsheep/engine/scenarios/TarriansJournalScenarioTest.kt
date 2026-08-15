package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.TarriansJournal
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.MayCastFromGraveyard
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.GrantStaticAbilityEffect
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tarrian's Journal // The Tomb of Aclazotz (LCI #126).
 *
 * The novel behavior proven here is the back face's graveyard-cast **entry rider**: a creature cast
 * from the graveyard under The Tomb's `MayCastFromGraveyard(Creature, entersWithCounter = FINALITY,
 * addedSubtypeOnEntry = "Vampire")` grant enters with a finality counter and gains Vampire "in
 * addition to its other types". The rider is frozen onto the stack spell at cast time and applied
 * on entry (StackResolver → EntersWithReplacements.applyCastFromGraveyardRider). The
 * finality-counter death-replacement (exile-instead-of-die) is an inherited, already-tested
 * mechanism, so this test asserts the counter is placed, not the later exile.
 */
class TarriansJournalScenarioTest : FunSpec({

    val projector = StateProjector()

    // A plain creature to seed in a graveyard and reanimate — a Rat, so "Vampire" is a genuinely
    // added subtype.
    val testRat = card("Test Rat") {
        manaCost = "{1}{B}"
        colorIdentity = "B"
        typeLine = "Creature — Rat"
        oracleText = ""
        power = 2
        toughness = 2
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TarriansJournal, testRat))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    // Put Tarrian's Journal down and transform it into The Tomb of Aclazotz (front ability [1]:
    // {2}, {T}, Discard your hand). The {T} taps it, so the resulting land is untapped for the
    // caller. Returns the (unchanged) entity id, now the back face.
    fun toTomb(driver: GameTestDriver, player: EntityId): EntityId {
        val journal = driver.putPermanentOnBattlefield(player, "Tarrian's Journal")
        driver.giveColorlessMana(player, 2)
        val transformId = TarriansJournal.activatedAbilities[1].id
        driver.submit(ActivateAbility(playerId = player, sourceId = journal, abilityId = transformId))
            .isSuccess shouldBe true
        driver.bothPass()
        resolveStack(driver)
        driver.untapPermanent(journal)
        return journal
    }

    test("front's sacrifice-to-draw ability is sorcery-speed ('activate only as a sorcery')") {
        TarriansJournal.activatedAbilities[0].timing shouldBe TimingRule.SorcerySpeed
    }

    test("'{2}, {T}, Discard your hand' transforms Tarrian's Journal into The Tomb of Aclazotz, a land") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val tomb = toTomb(driver, active)

        driver.state.getEntity(tomb)!!.get<CardComponent>()!!.name shouldBe "The Tomb of Aclazotz"
        projector.project(driver.state).hasType(tomb, "LAND") shouldBe true
    }

    test("a creature cast from the graveyard under The Tomb enters with a finality counter and is a Vampire") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val tomb = toTomb(driver, active)
        val rat = driver.putCardInGraveyard(active, "Test Rat")

        // {T}: You may cast a creature spell from your graveyard this turn (the reanimator ability).
        val backAbilities = TarriansJournal.backFace!!.activatedAbilities
        val reanimatorId = backAbilities.first { it.effect is GrantStaticAbilityEffect }.id
        driver.submit(ActivateAbility(playerId = active, sourceId = tomb, abilityId = reanimatorId))
            .isSuccess shouldBe true
        driver.bothPass()
        resolveStack(driver)

        val grantCount = driver.state.grantedStaticAbilities.count { it.ability is MayCastFromGraveyard }

        // Cast the rat from the graveyard for its mana cost ({1}{B}).
        driver.giveMana(active, Color.BLACK, 1)
        driver.giveColorlessMana(active, 1)
        val castRes = driver.castSpell(active, rat)
        withClue("grants=$grantCount castError=${castRes.error} paused=${driver.isPaused}") {
            castRes.isSuccess shouldBe true
        }
        driver.bothPass()
        resolveStack(driver)

        // It resolved onto the battlefield with the cast-this-way entry rider applied.
        val perm = driver.findPermanent(active, "Test Rat")
        perm shouldNotBe null
        driver.state.getEntity(perm!!)!!.get<CountersComponent>()!!
            .getCount(CounterType.FINALITY) shouldBe 1
        val projected = projector.project(driver.state)
        projected.isCreature(perm) shouldBe true
        projected.getSubtypes(perm).contains("Vampire") shouldBe true
    }
})
