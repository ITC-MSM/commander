package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.PerilousForays
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Tests for Perilous Forays (Ravnica: City of Guilds).
 *
 * Perilous Forays: {3}{G}{G}
 * Enchantment
 * {1}, Sacrifice a creature: Search your library for a land card with a basic land type,
 * put it onto the battlefield tapped, then shuffle.
 *
 * The interesting subtlety is the search filter: "a land card with a basic land type" is
 * broader than "a basic land" — it includes nonbasic lands whose type line carries a basic
 * land type (e.g. the Ravnica shocklands) but excludes typeless lands.
 */
class PerilousForaysTest : FunSpec({

    // A nonbasic land that carries basic land types (à la a Ravnica shockland).
    val DualBasicTypeLand = CardDefinition(
        name = "Test Dual Type Land",
        manaCost = ManaCost.ZERO,
        typeLine = TypeLine(
            cardTypes = setOf(CardType.LAND),
            subtypes = setOf(Subtype("Forest"), Subtype("Mountain"))
        ),
        oracleText = "{T}: Add {R} or {G}."
    )

    // A nonbasic land with no basic land type at all — must NOT be findable.
    val TypelessLand = CardDefinition(
        name = "Test Typeless Land",
        manaCost = ManaCost.ZERO,
        typeLine = TypeLine(cardTypes = setOf(CardType.LAND)),
        oracleText = "{T}: Add {C}."
    )

    val foraysAbilityId = PerilousForays.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(PerilousForays)
        driver.registerCard(DualBasicTypeLand)
        driver.registerCard(TypelessLand)
        return driver
    }

    test("searches a basic land and puts it onto the battlefield tapped") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Perilous Forays")
        val fodder = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")

        // Only land in the library is a Forest.
        driver.putCardOnTopOfLibrary(activePlayer, "Forest")

        driver.giveMana(activePlayer, Color.GREEN, 1)

        val source = driver.findPermanent(activePlayer, "Perilous Forays")!!
        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = source,
                abilityId = foraysAbilityId,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder))
            )
        )
        result.isSuccess shouldBe true

        // The creature was sacrificed to pay the cost.
        driver.findPermanent(activePlayer, "Grizzly Bears") shouldBe null

        driver.bothPass()

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.options.size shouldBe 1

        driver.submitDecision(activePlayer, CardsSelectedResponse(decision.id, decision.options))

        val forest = driver.findPermanent(activePlayer, "Forest")
        forest shouldNotBe null
        driver.isTapped(forest!!) shouldBe true
    }

    test("matches a nonbasic land with a basic land type but excludes typeless lands") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Perilous Forays")
        val fodder = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")

        // Library holds a typeless land and a dual land with basic land types.
        driver.putCardOnTopOfLibrary(activePlayer, "Test Typeless Land")
        driver.putCardOnTopOfLibrary(activePlayer, "Test Dual Type Land")

        driver.giveMana(activePlayer, Color.GREEN, 1)

        val source = driver.findPermanent(activePlayer, "Perilous Forays")!!
        driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = source,
                abilityId = foraysAbilityId,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder))
            )
        )

        driver.bothPass()

        // Only the dual land qualifies — the typeless land is filtered out.
        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.options.size shouldBe 1

        driver.submitDecision(activePlayer, CardsSelectedResponse(decision.id, decision.options))

        driver.findPermanent(activePlayer, "Test Dual Type Land") shouldNotBe null
        driver.findPermanent(activePlayer, "Test Typeless Land") shouldBe null
    }
})
