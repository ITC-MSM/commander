package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.hob.cards.ThranduilTheElvenking
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Thranduil, the Elvenking — {2}{B}{G}{U} Legendary Creature — Elf Noble (The Hobbit #167).
 *
 *  - "Thranduil has all activated abilities of all Elf cards in your graveyard."
 *  - "Whenever another legendary Elf you control enters, draw two cards, then discard a card."
 *
 * The first ability is the `DonorCards.YOUR_GRAVEYARD` arm of `HasAllActivatedAbilitiesOfCards`.
 * These tests pin the parts of that arm that could silently regress: the `cardFilter` narrowing,
 * the "**your** graveyard" ownership scope, the live re-read as the graveyard changes, and the
 * CR 113.7 source binding (the granted `{T}` taps *Thranduil*, not the donor card).
 */
class ThranduilTheElvenkingScenarioTest : FunSpec({

    // Donor: an Elf whose only ability is activated and has a {T} in its cost, so activating it
    // through Thranduil proves both the grant and the source binding.
    val elfHealer = card("Thranduil Test Elf Healer") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Elf Cleric"
        power = 1
        toughness = 1
        oracleText = "{T}: You gain 2 life."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.GainLife(2)
        }
    }
    val healerAbilityId = elfHealer.activatedAbilities[0].id

    // Same shape, but NOT an Elf — the cardFilter must exclude it.
    val goblinTinkerer = card("Thranduil Test Goblin Tinkerer") {
        manaCost = "{1}{R}"
        typeLine = "Creature — Goblin Artificer"
        power = 1
        toughness = 1
        oracleText = "{T}: You gain 2 life."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.GainLife(2)
        }
    }
    val tinkererAbilityId = goblinTinkerer.activatedAbilities[0].id

    // Trigger fodder: a legendary Elf, a nonlegendary Elf, and a legendary non-Elf.
    val legendaryElf = card("Thranduil Test Elven Lord") {
        manaCost = "{G}"
        typeLine = "Legendary Creature — Elf Noble"
        power = 1
        toughness = 1
    }
    val plainElf = card("Thranduil Test Elf Scout") {
        manaCost = "{G}"
        typeLine = "Creature — Elf Scout"
        power = 1
        toughness = 1
    }
    val legendaryDwarf = card("Thranduil Test Dwarf Lord") {
        manaCost = "{G}"
        typeLine = "Legendary Creature — Dwarf Noble"
        power = 1
        toughness = 1
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(
                ThranduilTheElvenking, elfHealer, goblinTinkerer,
                legendaryElf, plainElf, legendaryDwarf
            )
        )
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /**
     * Thranduil on the battlefield and able to use a `{T}` ability. Clearing summoning sickness is
     * load-bearing for the *negative* tests too: without it a missing grant and an unusable `{T}`
     * both fail, and the assertion would pass for the wrong reason.
     */
    fun readyThranduil(driver: GameTestDriver, you: com.wingedsheep.sdk.model.EntityId) =
        driver.putCreatureOnBattlefield(you, "Thranduil, the Elvenking")
            .also { driver.removeSummoningSickness(it) }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (driver.state.stack.isNotEmpty() && driver.state.pendingDecision == null && guard++ < 20) {
            driver.bothPass()
        }
    }

    test("an Elf card in your graveyard lends its activated ability, and the {T} taps Thranduil") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val thranduil = readyThranduil(driver, you)
        driver.putCardInGraveyard(you, "Thranduil Test Elf Healer")

        val lifeBefore = driver.getLifeTotal(you)
        driver.submitSuccess(ActivateAbility(playerId = you, sourceId = thranduil, abilityId = healerAbilityId))
        resolveStack(driver)

        driver.getLifeTotal(you) shouldBe lifeBefore + 2
        // CR 113.7 — the granted ability's source is Thranduil, so its {T} cost tapped him.
        driver.state.getEntity(thranduil)?.has<TappedComponent>() shouldBe true
    }

    test("a non-Elf card in your graveyard lends nothing (cardFilter narrows the donor pool)") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val thranduil = readyThranduil(driver, you)
        driver.putCardInGraveyard(you, "Thranduil Test Goblin Tinkerer")

        driver.submitExpectFailure(
            ActivateAbility(playerId = you, sourceId = thranduil, abilityId = tinkererAbilityId)
        )
    }

    test("an Elf card in an OPPONENT's graveyard lends nothing (\"your\" graveyard only)") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        val thranduil = readyThranduil(driver, you)
        driver.putCardInGraveyard(opponent, "Thranduil Test Elf Healer")

        driver.submitExpectFailure(
            ActivateAbility(playerId = you, sourceId = thranduil, abilityId = healerAbilityId)
        )
    }

    test("the donor pool is re-read live — the ability appears when an Elf hits the graveyard mid-game") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val thranduil = readyThranduil(driver, you)
        val healerOnBoard = driver.putCreatureOnBattlefield(you, "Thranduil Test Elf Healer")

        // Nothing in the graveyard yet: the ability isn't Thranduil's to activate. (Activating the
        // Healer's own printed copy would tap the Healer, so this proves the *grant* is absent.)
        driver.submitExpectFailure(
            ActivateAbility(playerId = you, sourceId = thranduil, abilityId = healerAbilityId)
        )

        // A real zone change puts the Elf in the graveyard; the grant must appear without any
        // re-projection prompt, because the pool is read on every legality query.
        driver.moveToGraveyard(healerOnBoard)
        driver.getGraveyard(you).contains(healerOnBoard) shouldBe true

        val lifeBefore = driver.getLifeTotal(you)
        driver.submitSuccess(ActivateAbility(playerId = you, sourceId = thranduil, abilityId = healerAbilityId))
        resolveStack(driver)
        driver.getLifeTotal(you) shouldBe lifeBefore + 2
    }

    test("another legendary Elf entering draws two cards, then discards one") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.putCreatureOnBattlefield(you, "Thranduil, the Elvenking")
        val elf = driver.putCardInHand(you, "Thranduil Test Elven Lord")

        val handBefore = driver.getHandSize(you)
        val graveBefore = driver.getGraveyard(you).size
        driver.giveMana(you, Color.GREEN)
        driver.castSpell(you, elf).isSuccess shouldBe true
        driver.bothPass() // Resolve the Elf.
        resolveStack(driver) // Resolve the draw-two trigger, pausing on the discard choice.

        // Cast one (-1), drew two (+2), then discard one (-1) → net level with the starting hand.
        (driver.pendingDecision != null) shouldBe true
        val toDiscard = driver.getHand(you).first()
        driver.submitCardSelection(you, listOf(toDiscard)).isSuccess shouldBe true
        resolveStack(driver)

        driver.getHandSize(you) shouldBe handBefore
        driver.getGraveyard(you).size shouldBe graveBefore + 1
    }

    test("a nonlegendary Elf and a legendary non-Elf entering do not trigger") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.putCreatureOnBattlefield(you, "Thranduil, the Elvenking")
        val scout = driver.putCardInHand(you, "Thranduil Test Elf Scout")
        val dwarf = driver.putCardInHand(you, "Thranduil Test Dwarf Lord")

        var handBefore = driver.getHandSize(you)
        driver.giveMana(you, Color.GREEN)
        driver.castSpell(you, scout).isSuccess shouldBe true
        resolveStack(driver)
        driver.state.pendingDecision shouldBe null
        driver.getHandSize(you) shouldBe handBefore - 1

        handBefore = driver.getHandSize(you)
        driver.giveMana(you, Color.GREEN)
        driver.castSpell(you, dwarf).isSuccess shouldBe true
        resolveStack(driver)
        driver.state.pendingDecision shouldBe null
        driver.getHandSize(you) shouldBe handBefore - 1
    }

    test("Thranduil entering does not trigger his own ability (\"another\")") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val thranduil = driver.putCardInHand(you, "Thranduil, the Elvenking")

        val handBefore = driver.getHandSize(you)
        driver.giveMana(you, Color.BLACK)
        driver.giveMana(you, Color.GREEN)
        driver.giveMana(you, Color.BLUE)
        driver.giveColorlessMana(you, 2)
        driver.castSpell(you, thranduil).isSuccess shouldBe true
        resolveStack(driver)

        driver.state.pendingDecision shouldBe null
        driver.getHandSize(you) shouldBe handBefore - 1
    }
})
