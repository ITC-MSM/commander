package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.Colossadactyl
import com.wingedsheep.mtg.sets.definitions.lci.cards.IntrepidPaleontologist
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Intrepid Paleontologist (LCI #193):
 *   {T}: Add one mana of any color.
 *   {2}: Exile target card from a graveyard.
 *   You may cast Dinosaur creature spells from among cards you own exiled with this creature.
 *   If you cast a spell this way, that creature enters with a finality counter on it.
 *
 * Exercises the {2} linked-exile activated ability, the Dinosaur-only cast permission
 * (GrantMayCastFromLinkedExile with ownedByYou), and the new cast-this-way finality-counter
 * entry rider on that grant.
 */
class IntrepidPaleontologistScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(IntrepidPaleontologist, Colossadactyl))
        return driver
    }

    // The {2} exile ability is the second activated ability (index 0 is the mana ability).
    val exileAbilityId = IntrepidPaleontologist.activatedAbilities[1].id

    fun linkedExileOf(driver: GameTestDriver, sourceId: EntityId): List<EntityId> =
        driver.state.getEntity(sourceId)?.get<LinkedExileComponent>()?.exiledIds ?: emptyList()

    test("{2}: Exile target card from a graveyard links it to the Paleontologist") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val paleo = driver.putCreatureOnBattlefield(you, "Intrepid Paleontologist")
        val dino = driver.putCardInGraveyard(you, "Colossadactyl")

        driver.giveMana(you, Color.GREEN, 2)
        driver.submitSuccess(
            ActivateAbility(
                playerId = you,
                sourceId = paleo,
                abilityId = exileAbilityId,
                targets = listOf(ChosenTarget.Card(dino, ownerId = you, zone = Zone.GRAVEYARD))
            )
        )
        driver.bothPass() // resolve the exile ability

        driver.getGraveyard(you) shouldNotContain dino
        driver.getExile(you) shouldContain dino
        linkedExileOf(driver, paleo) shouldContain dino
    }

    test("A Dinosaur cast from linked exile resolves onto the battlefield with a finality counter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val paleo = driver.putCreatureOnBattlefield(you, "Intrepid Paleontologist")
        val dino = driver.putCardInGraveyard(you, "Colossadactyl")

        // {2}: exile the Dinosaur, linked to the Paleontologist.
        driver.giveMana(you, Color.GREEN, 2)
        driver.submitSuccess(
            ActivateAbility(
                playerId = you,
                sourceId = paleo,
                abilityId = exileAbilityId,
                targets = listOf(ChosenTarget.Card(dino, ownerId = you, zone = Zone.GRAVEYARD))
            )
        )
        driver.bothPass()
        driver.getExile(you) shouldContain dino

        // Cast Colossadactyl ({2}{G}{G}) from exile via the granted permission.
        driver.giveMana(you, Color.GREEN, 4)
        driver.castSpell(you, dino).isSuccess shouldBe true

        // Resolve it onto the battlefield.
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()

        val perm = driver.findPermanent(you, "Colossadactyl")
        perm shouldNotBe null
        // "If you cast a spell this way, that creature enters with a finality counter on it."
        driver.state.getEntity(perm!!)!!.get<CountersComponent>()
            ?.getCount(CounterType.FINALITY) shouldBe 1
        // The exile pile no longer holds it.
        linkedExileOf(driver, paleo) shouldNotContain dino
    }

    test("A non-Dinosaur card exiled with the Paleontologist cannot be cast from exile") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val paleo = driver.putCreatureOnBattlefield(you, "Intrepid Paleontologist")
        // Grizzly Bears is a 2/2 Bear — not a Dinosaur, so the grant's filter excludes it.
        val bears = driver.putCardInGraveyard(you, "Grizzly Bears")

        driver.giveMana(you, Color.GREEN, 2)
        driver.submitSuccess(
            ActivateAbility(
                playerId = you,
                sourceId = paleo,
                abilityId = exileAbilityId,
                targets = listOf(ChosenTarget.Card(bears, ownerId = you, zone = Zone.GRAVEYARD))
            )
        )
        driver.bothPass()
        driver.getExile(you) shouldContain bears

        // No permission to cast a non-Dinosaur from the linked exile.
        driver.giveMana(you, Color.GREEN, 2)
        driver.castSpell(you, bears).isSuccess shouldBe false
    }
})
