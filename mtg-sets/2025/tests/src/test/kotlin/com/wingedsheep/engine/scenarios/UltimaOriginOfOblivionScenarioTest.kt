package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.UltimaOriginOfOblivion
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.basicLand
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Ultima, Origin of Oblivion ({5}, 4/4 Legendary God with Flying).
 *
 *   Whenever Ultima attacks, put a blight counter on target land. For as long as that land has a
 *   blight counter on it, it loses all land types and abilities and has "{T}: Add {C}."
 *   Whenever you tap a land for {C}, add an additional {C}.
 *
 * Exercises two new SDK/engine primitives:
 *  - [com.wingedsheep.sdk.scripting.Duration.WhileAffectedHasCounter] — the counter-keyed,
 *    source-independent (CR 611.2b) continuous transform, applied via a floating
 *    [com.wingedsheep.sdk.scripting.effects.BecomeArtifactEffect].
 *  - [com.wingedsheep.sdk.scripting.TappedForManaType.COLORLESS] gate on
 *    [com.wingedsheep.sdk.scripting.AdditionalManaOnSourceTap] — "tap a land for {C}".
 */
class UltimaOriginOfOblivionScenarioTest : FunSpec({

    // Wastes — the colorless basic land (auto "{T}: Add {C}"), matching "tap a land for {C}".
    val ColorlessWaste = basicLand("Wastes") {}

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(UltimaOriginOfOblivion, ColorlessWaste))
        return driver
    }

    /** The (single) mana ability the given land offers as a legal action for [you]. */
    fun GameTestDriver.landManaAbility(you: EntityId, land: EntityId): ActivateAbility =
        LegalActionEnumerator.create(cardRegistry)
            .enumerate(state, you, EnumerationMode.FULL)
            .mapNotNull { it.action as? ActivateAbility }
            .first { it.sourceId == land }

    /** Declare Ultima as the sole attacker and pick [land] for the attack trigger. */
    fun GameTestDriver.attackAndBlight(you: EntityId, ultima: EntityId, opponent: EntityId, land: EntityId) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        declareAttackers(you, listOf(ultima), opponent)
        var guard = 0
        while (pendingDecision is ChooseTargetsDecision && guard++ < 5) {
            submitTargetSelection(you, listOf(land))
        }
        guard = 0
        while (state.stack.isNotEmpty() && !isPaused && guard++ < 20) bothPass()
    }

    test("attacking blights target land: it loses its land types and abilities, keeps LAND, gains a blight counter") {
        val d = createDriver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ultima = d.putCreatureOnBattlefield(you, "Ultima, Origin of Oblivion")
        d.removeSummoningSickness(ultima)
        val forest = d.putPermanentOnBattlefield(you, "Forest")

        d.attackAndBlight(you, ultima, opponent, forest)

        // Blight counter placed.
        d.state.getEntity(forest)?.get<CountersComponent>()?.getCount(CounterType.BLIGHT) shouldBe 1

        val projected = d.state.projectedState
        // Still a land (card type kept), but no longer a Forest (land types stripped).
        projected.hasType(forest, "LAND") shouldBe true
        projected.hasSubtype(forest, "Forest") shouldBe false
        // Lost all abilities (its intrinsic {T}: Add {G} is gone).
        projected.hasLostAllAbilities(forest) shouldBe true

        // Its only remaining mana ability is the granted {T}: Add {C}.
        val enumerator = LegalActionEnumerator.create(d.cardRegistry)
        val actions = enumerator.enumerate(d.state, you, EnumerationMode.FULL)
        val forestAbilities = actions.mapNotNull { it.action as? ActivateAbility }.filter { it.sourceId == forest }
        forestAbilities.size shouldBe 1
    }

    test("the blight transform persists after Ultima leaves the battlefield (CR 611.2b)") {
        val d = createDriver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ultima = d.putCreatureOnBattlefield(you, "Ultima, Origin of Oblivion")
        d.removeSummoningSickness(ultima)
        val forest = d.putPermanentOnBattlefield(you, "Forest")

        d.attackAndBlight(you, ultima, opponent, forest)
        // Ultima dies — the transform is a resolved continuous effect, not Ultima's static, so it stays.
        d.moveToGraveyard(ultima)

        val projected = d.state.projectedState
        projected.hasSubtype(forest, "Forest") shouldBe false
        projected.hasLostAllAbilities(forest) shouldBe true
        d.state.getEntity(forest)?.get<CountersComponent>()?.getCount(CounterType.BLIGHT) shouldBe 1
    }

    test("removing the blight counter reverts the land to its original characteristics") {
        val d = createDriver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ultima = d.putCreatureOnBattlefield(you, "Ultima, Origin of Oblivion")
        d.removeSummoningSickness(ultima)
        val forest = d.putPermanentOnBattlefield(you, "Forest")

        d.attackAndBlight(you, ultima, opponent, forest)
        d.state.projectedState.hasSubtype(forest, "Forest") shouldBe false

        // Strip the blight counter — the "for as long as it has a blight counter" duration ends.
        d.replaceState(d.state.updateEntity(forest) { it.with(CountersComponent(emptyMap())) })

        val projected = d.state.projectedState
        projected.hasSubtype(forest, "Forest") shouldBe true
        projected.hasLostAllAbilities(forest) shouldBe false
    }

    test("Ultima's third ability adds an extra {C} when you tap a land for {C}") {
        val d = createDriver()
        d.initMirrorMatch(deck = Deck.of("Wastes" to 40), startingLife = 20)
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(you, "Ultima, Origin of Oblivion")
        val waste = d.putPermanentOnBattlefield(you, "Wastes")

        d.submit(d.landManaAbility(you, waste)).isSuccess shouldBe true

        // 1 from the land + 1 from Ultima's bonus.
        d.state.getEntity(you)?.get<ManaPoolComponent>()?.colorless shouldBe 2
    }

    test("Ultima's third ability does NOT fire when a land is tapped for colored mana") {
        val d = createDriver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(you, "Ultima, Origin of Oblivion")
        val forest = d.putPermanentOnBattlefield(you, "Forest")

        d.submit(d.landManaAbility(you, forest)).isSuccess shouldBe true

        val pool = d.state.getEntity(you)?.get<ManaPoolComponent>()
        pool?.green shouldBe 1     // just the Forest — colored taps don't trigger Ultima
        pool?.colorless shouldBe 0
    }

    test("end-to-end: tapping a land Ultima blighted yields two {C} (granted ability + bonus)") {
        val d = createDriver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ultima = d.putCreatureOnBattlefield(you, "Ultima, Origin of Oblivion")
        d.removeSummoningSickness(ultima)
        val forest = d.putPermanentOnBattlefield(you, "Forest")

        d.attackAndBlight(you, ultima, opponent, forest)

        // The blighted forest now taps for {C}; find that granted ability and activate it.
        val enumerator = LegalActionEnumerator.create(d.cardRegistry)
        val grantedTap = enumerator.enumerate(d.state, you, EnumerationMode.FULL)
            .mapNotNull { it.action as? ActivateAbility }
            .first { it.sourceId == forest }
        d.submit(grantedTap).isSuccess shouldBe true

        // 1 {C} from the granted ability + 1 {C} from Ultima's "tap a land for {C}" bonus.
        d.state.getEntity(you)?.get<ManaPoolComponent>()?.colorless shouldBe 2
    }
})
