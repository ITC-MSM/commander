package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.TenthDistrictHero
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tenth District Hero (MKM #34) — {1}{W} 2/3 Human.
 *
 * "{1}{W}, Collect evidence 2: This creature becomes a Human Detective with base power and
 *  toughness 4/4 and gains vigilance."
 * "{2}{W}, Collect evidence 4: If this creature is a Detective, it becomes a legendary creature
 *  named Mileva, the Stalwart, it has base power and toughness 5/5, and it gains 'Other creatures
 *  you control have indestructible.'"
 *
 * Four claims, each with a plausible wrong implementation:
 *
 *  - the first ability has **no duration**, so a 4/4 vigilant Detective must survive the cleanup
 *    step — the default on almost every animate-style effect in the SDK is `EndOfTurn`, and an
 *    author who takes it produces a card that silently reverts;
 *  - the second ability's "if this creature is a Detective" is checked **on resolution**, not on
 *    activation (printed ruling), so it must be activatable off a bare Human and simply do
 *    nothing — modelling it as an `ActivationRestriction` would be illegal-instead-of-inert;
 *  - the granted "other creatures you control have indestructible" is a static handed to the
 *    permanent, so it must reach *other* creatures and not the Hero itself;
 *  - collect evidence is a genuine cost (CR 701.59b), so a graveyard that can't reach the total
 *    makes the ability unactivatable rather than free.
 */
class TenthDistrictHeroScenarioTest : FunSpec({

    val becomeDetective = TenthDistrictHero.activatedAbilities[0].id
    val becomeMileva = TenthDistrictHero.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("the first ability makes a permanent 4/4 vigilant Human Detective") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val hero = driver.putPermanentOnBattlefield(player, "Tenth District Hero")
        repeat(2) { driver.putLandOnBattlefield(player, "Plains") }
        // Centaur Courser is {2}{G} — mana value 3, over the collect-evidence-2 threshold alone.
        driver.putCardInGraveyard(player, "Centaur Courser")

        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = hero,
                abilityId = becomeDetective,
                paymentStrategy = PaymentStrategy.AutoPay,
            )
        ).error shouldBe null
        driver.bothPass()

        val projected = driver.state.projectedState
        withClue("base power and toughness are set to 4/4") {
            projected.getPower(hero) shouldBe 4
            projected.getToughness(hero) shouldBe 4
        }
        withClue("it is a Human Detective and gained vigilance") {
            projected.hasSubtype(hero, "Human") shouldBe true
            projected.hasSubtype(hero, "Detective") shouldBe true
            projected.hasKeyword(hero, Keyword.VIGILANCE) shouldBe true
        }
        withClue("the evidence was actually exiled as a cost") {
            driver.getExileCardNames(player) shouldBe listOf("Centaur Courser")
        }
    }

    test("the first ability has no duration — it survives end of turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val hero = driver.putPermanentOnBattlefield(player, "Tenth District Hero")
        repeat(2) { driver.putLandOnBattlefield(player, "Plains") }
        driver.putCardInGraveyard(player, "Centaur Courser")

        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = hero,
                abilityId = becomeDetective,
                paymentStrategy = PaymentStrategy.AutoPay,
            )
        ).error shouldBe null
        driver.bothPass()
        driver.passPriorityUntil(Step.UPKEEP)

        withClue("the printed ruling: the effect lasts until the game ends or it leaves") {
            driver.state.projectedState.getPower(hero) shouldBe 4
            driver.state.projectedState.hasSubtype(hero, "Detective") shouldBe true
            driver.state.projectedState.hasKeyword(hero, Keyword.VIGILANCE) shouldBe true
        }
    }

    test("the second ability renames it and hands out indestructible to other creatures") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val hero = driver.putPermanentOnBattlefield(player, "Tenth District Hero")
        val bears = driver.putPermanentOnBattlefield(player, "Grizzly Bears")
        repeat(5) { driver.putLandOnBattlefield(player, "Plains") }
        driver.putCardInGraveyard(player, "Centaur Courser")
        driver.putCardInGraveyard(player, "Centaur Courser")
        driver.putCardInGraveyard(player, "Centaur Courser")

        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = hero,
                abilityId = becomeDetective,
                paymentStrategy = PaymentStrategy.AutoPay,
            )
        ).error shouldBe null
        driver.bothPass()

        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = hero,
                abilityId = becomeMileva,
                paymentStrategy = PaymentStrategy.AutoPay,
            )
        ).error shouldBe null
        driver.bothPass()

        val projected = driver.state.projectedState
        withClue("legendary, renamed, and 5/5") {
            projected.isLegendary(hero) shouldBe true
            projected.getName(hero) shouldBe "Mileva, the Stalwart"
            projected.getPower(hero) shouldBe 5
            projected.getToughness(hero) shouldBe 5
        }
        withClue("the first ability's Detective type and vigilance are untouched") {
            projected.hasSubtype(hero, "Detective") shouldBe true
            projected.hasKeyword(hero, Keyword.VIGILANCE) shouldBe true
        }
        withClue("'*Other* creatures you control' — the Bears, not the Hero") {
            projected.hasKeyword(bears, Keyword.INDESTRUCTIBLE) shouldBe true
            projected.hasKeyword(hero, Keyword.INDESTRUCTIBLE) shouldBe false
        }
    }

    test("the second ability is activatable off a bare Human and simply does nothing") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val hero = driver.putPermanentOnBattlefield(player, "Tenth District Hero")
        repeat(3) { driver.putLandOnBattlefield(player, "Plains") }
        driver.putCardInGraveyard(player, "Centaur Courser")
        driver.putCardInGraveyard(player, "Centaur Courser")

        withClue("the ruling: it may be activated regardless of creature type") {
            driver.submit(
                ActivateAbility(
                    playerId = player,
                    sourceId = hero,
                    abilityId = becomeMileva,
                    paymentStrategy = PaymentStrategy.AutoPay,
                )
            ).error shouldBe null
        }
        driver.bothPass()

        val projected = driver.state.projectedState
        withClue("it isn't a Detective, so the whole effect is inert") {
            projected.getName(hero) shouldBe null
            projected.isLegendary(hero) shouldBe false
            projected.getPower(hero) shouldBe 2
            projected.getToughness(hero) shouldBe 3
        }
        withClue("the cost was still paid — the ability resolved, it just did nothing") {
            driver.getExileCardNames(player).size shouldBe 2
        }
    }

    test("a graveyard under the threshold makes the ability unactivatable") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val hero = driver.putPermanentOnBattlefield(player, "Tenth District Hero")
        repeat(2) { driver.putLandOnBattlefield(player, "Plains") }

        val result = driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = hero,
                abilityId = becomeDetective,
                paymentStrategy = PaymentStrategy.AutoPay,
            )
        )
        withClue("CR 701.59b: you can't choose to collect evidence you can't pay for") {
            (result.error != null) shouldBe true
            driver.state.projectedState.getPower(hero) shouldBe 2
        }
    }
})
