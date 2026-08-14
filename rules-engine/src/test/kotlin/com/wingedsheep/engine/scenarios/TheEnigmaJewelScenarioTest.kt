package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CraftedFromExiledComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.TheEnigmaJewel
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * The Enigma Jewel // Locus of Enlightenment (LCI #55) — {U} Legendary Artifact // Legendary Artifact.
 *
 * The *whole* card is implemented and covered here — every printed clause has a paired test:
 *
 * Front — The Enigma Jewel:
 *  - "The Enigma Jewel enters tapped." ([EntersTapped] replacement, exercised via a real cast).
 *  - "{T}: Add {C}{C}. Spend this mana only to activate abilities."
 *    ([ManaRestriction.AbilityActivationOnly]) — funds any source's ability activation, never a spell.
 *  - "Craft with four or more nonlands with activated abilities {8}{U}" (CR 702.167a/b): the material
 *    filter `Nonland + CardPredicate.HasActivatedAbility`. A mana-only artifact qualifies (the
 *    predicate counts mana abilities); a vanilla nonland and a land-with-an-ability do not; fewer than
 *    four is rejected; graveyard cards are eligible material. Craft returns the card transformed.
 *
 * Back — Locus of Enlightenment:
 *  - "…has each activated ability of the exiled cards used to craft it" (CR 702.167c): the granted
 *    ability is usable ON the Locus — a `{T}` cost taps the Locus (source binds to it, CR 707.10b).
 *  - "…only once each turn" — a granted ability is spent for the turn after one activation and
 *    refreshes next turn; two exiled *copies* of one card get *separate* once-per-turn budgets.
 *  - "Whenever you activate an ability that isn't a mana ability, copy it. You may choose new targets
 *    for the copy." — targeted ability copied with a new-target prompt (CR 707.10c), untargeted ability
 *    copied without a prompt, mana ability never fires the trigger (CR 605.1a).
 */
class TheEnigmaJewelScenarioTest : FunSpec({

    // ---- Craft materials: nonland artifacts with activated abilities (distinct life gains so we can
    // tell which granted ability resolved). All non-mana, non-tap so the once-each-turn cap is the
    // only thing limiting re-activation. ----
    fun payCache(name: String, gain: Int) = card(name) {
        manaCost = "{0}"
        typeLine = "Artifact"
        oracleText = "{1}: You gain $gain life."
        activatedAbility {
            cost = Costs.Mana("{1}")
            effect = Effects.GainLife(gain)
            timing = TimingRule.InstantSpeed
        }
    }
    val cacheA = payCache("Enigma Cache A", 1)
    val cacheB = payCache("Enigma Cache B", 2)
    val cacheC = payCache("Enigma Cache C", 4)
    val cacheD = payCache("Enigma Cache D", 8)

    // {T}: You gain 1 life — a tap ability, to prove the granted ability's {T} taps the *Locus*.
    val tapCache = card("Enigma Tap Cache") {
        manaCost = "{0}"
        typeLine = "Artifact"
        oracleText = "{T}: You gain 1 life."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.GainLife(1)
            timing = TimingRule.InstantSpeed
        }
    }

    // A mana-only artifact — has ONLY a mana ability. It must still qualify as craft material
    // (HasActivatedAbility counts mana abilities, unlike HasNonManaActivatedAbility).
    val manaCache = card("Enigma Mana Cache") {
        manaCost = "{0}"
        typeLine = "Artifact"
        oracleText = "{T}: Add {C}."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.AddColorlessMana(1)
            manaAbility = true
            timing = TimingRule.ManaAbility
        }
    }

    // A vanilla nonland with NO activated ability — must NOT qualify as craft material.
    val bauble = card("Enigma Bauble") {
        manaCost = "{0}"
        typeLine = "Artifact"
        oracleText = ""
    }

    // A LAND with an activated ability — has an ability but is a land, so must NOT qualify.
    val abilityLand = card("Enigma Ability Land") {
        typeLine = "Land"
        oracleText = "{T}: You gain 1 life."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.GainLife(1)
            timing = TimingRule.InstantSpeed
        }
    }

    // A nonland whose ONLY activated ability functions from the GRAVEYARD (not the battlefield). It
    // still "has an activated ability" for craft-material purposes — the material pool includes cards
    // in the graveyard, and a graveyard-activated ability is one such card's activated ability.
    val graveyardOnlyCache = card("Enigma Graveyard Cache") {
        manaCost = "{0}"
        typeLine = "Artifact"
        oracleText = "{1}: You gain 1 life. Activate only from your graveyard."
        activatedAbility {
            cost = Costs.Mana("{1}")
            effect = Effects.GainLife(1)
            activateFromZone = Zone.GRAVEYARD
            timing = TimingRule.InstantSpeed
        }
    }

    // ---- Copy-trigger fixtures (from the back face's copy clause). ----
    // {2}: You gain 1 life — a mana-cost ability the Jewel's restricted {C}{C} can fund.
    val testCurio = card("Test Curio") {
        manaCost = "{0}"
        typeLine = "Artifact"
        oracleText = "{2}: You gain 1 life."
        activatedAbility {
            cost = Costs.Mana("{2}")
            effect = Effects.GainLife(1)
            timing = TimingRule.InstantSpeed
        }
    }
    val testTrinket = CardDefinition.artifact(name = "Test Trinket", manaCost = ManaCost.parse("{2}"))
    // {T}: Target creature you control gets +1/+0 — drives the copy trigger's targeted path.
    val testPumper = card("Test Pumper") {
        manaCost = "{1}"
        typeLine = "Creature — Soldier"
        power = 1
        toughness = 1
        oracleText = "{T}: Target creature you control gets +1/+0 until end of turn."
        activatedAbility {
            cost = AbilityCost.Tap
            effect = Effects.ModifyStats(1, 0, EffectTarget.ContextTarget(0))
            target = Targets.CreatureYouControl
            timing = TimingRule.InstantSpeed
        }
    }
    // {T}: You gain 1 life — drives the copy trigger's untargeted path.
    val testLifegainer = card("Test Lifegainer") {
        manaCost = "{1}"
        typeLine = "Creature — Cleric"
        power = 1
        toughness = 1
        oracleText = "{T}: You gain 1 life."
        activatedAbility {
            cost = AbilityCost.Tap
            effect = Effects.GainLife(1)
            timing = TimingRule.InstantSpeed
        }
    }

    val extraCards = listOf(
        cacheA, cacheB, cacheC, cacheD, tapCache, manaCache, bauble, abilityLand, graveyardOnlyCache,
        testCurio, testTrinket, testPumper, testLifegainer
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + extraCards)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    val manaAbilityId = TheEnigmaJewel.activatedAbilities.single { it.isManaAbility }.id
    val craftAbilityId = TheEnigmaJewel.activatedAbilities.single { !it.isManaAbility }.id

    fun GameTestDriver.tapJewelForRestrictedMana(playerId: EntityId): EntityId {
        val jewel = putPermanentOnBattlefield(playerId, "The Enigma Jewel")
        submitSuccess(ActivateAbility(playerId, jewel, manaAbilityId))
        return jewel
    }

    /** The activation actions the engine currently offers on [source] for [player] (respects
     *  affordability + once-each-turn), so tests never hard-code synthesized granted-ability ids. */
    fun GameTestDriver.abilitiesOn(player: EntityId, source: EntityId): List<ActivateAbility> =
        legalActions(player).mapNotNull { it.action as? ActivateAbility }.filter { it.sourceId == source }

    // A granted ability's synthesized id is "donor_<donorEntity>_<printedAbilityId>", so this
    // fragment identifies the ability contributed by a specific exiled material entity.
    fun fromMaterial(material: EntityId) = "donor_${material.value}_"

    /** Craft The Enigma Jewel using [materials]; returns the (same) entity, now the Locus. */
    fun GameTestDriver.craftJewel(player: EntityId, jewel: EntityId, materials: List<EntityId>): EntityId {
        giveMana(player, Color.BLUE, 9) // {8}{U}
        submitSuccess(
            ActivateAbility(
                playerId = player,
                sourceId = jewel,
                abilityId = craftAbilityId,
                costPayment = AdditionalCostPayment(exiledCards = materials)
            )
        )
        bothPass() // resolve the craft ability → return transformed as the Locus
        return jewel
    }

    // =====================================================================================
    // Front face
    // =====================================================================================

    test("The Enigma Jewel enters tapped when cast") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewelInHand = driver.putCardInHand(me, "The Enigma Jewel")
        driver.giveMana(me, Color.BLUE, 1)
        driver.castSpell(me, jewelInHand)
        driver.bothPass()

        val jewel = driver.state.getEntity(jewelInHand)
        jewel.shouldNotBeNull()
        jewel.has<TappedComponent>() shouldBe true
    }

    test("{T}: Add {C}{C} puts two colorless mana restricted to ability activation in the pool") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.tapJewelForRestrictedMana(me)

        driver.state.getEntity(jewel)!!.has<TappedComponent>() shouldBe true
        val pool = driver.state.getEntity(me)?.get<ManaPoolComponent>()
        pool.shouldNotBeNull()
        pool.restrictedMana.size shouldBe 2
        pool.restrictedMana.all { it.color == null } shouldBe true
        pool.restrictedMana.all { it.restriction == ManaRestriction.AbilityActivationOnly } shouldBe true
        pool.colorless shouldBe 0
    }

    test("the restricted mana pays for an activated ability of another source") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val curio = driver.putPermanentOnBattlefield(me, "Test Curio")
        driver.tapJewelForRestrictedMana(me)
        val lifeBefore = driver.getLifeTotal(me)

        val gainAbilityId = driver.cardRegistry.requireCard("Test Curio").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = curio, abilityId = gainAbilityId))

        driver.state.getEntity(me)?.get<ManaPoolComponent>()!!.restrictedMana.size shouldBe 0
        driver.bothPass()
        driver.getLifeTotal(me) shouldBe lifeBefore + 1
    }

    test("the restricted mana can NOT pay for a spell") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        driver.tapJewelForRestrictedMana(me)

        val trinket = driver.putCardInHand(me, "Test Trinket")
        val result = driver.submit(
            CastSpell(playerId = me, cardId = trinket, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.isSuccess shouldBe false
    }

    // =====================================================================================
    // Craft (material filter: four or more nonlands with activated abilities)
    // =====================================================================================

    test("craft transforms into the Locus with four valid materials — a mana-only artifact qualifies") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.putPermanentOnBattlefield(me, "The Enigma Jewel")
        // Includes the mana-only Enigma Mana Cache: HasActivatedAbility counts mana abilities.
        val materials = listOf("Enigma Cache A", "Enigma Cache B", "Enigma Cache C", "Enigma Mana Cache")
            .map { driver.putPermanentOnBattlefield(me, it) }

        driver.craftJewel(me, jewel, materials)

        // The entity is now the back face, and the materials are exiled and recorded.
        val card = driver.state.getEntity(jewel)?.get<CardComponent>()
        card.shouldNotBeNull()
        card.name shouldBe "Locus of Enlightenment"
        materials.forEach { driver.state.getZone(ZoneKey(me, Zone.EXILE)).contains(it) shouldBe true }
        driver.state.getEntity(jewel)?.get<CraftedFromExiledComponent>()?.exiledIds shouldBe materials
    }

    test("a nonland WITHOUT an activated ability is not valid craft material") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.putPermanentOnBattlefield(me, "The Enigma Jewel")
        val valid = listOf("Enigma Cache A", "Enigma Cache B", "Enigma Cache C")
            .map { driver.putPermanentOnBattlefield(me, it) }
        val vanilla = driver.putPermanentOnBattlefield(me, "Enigma Bauble") // no ability
        driver.giveMana(me, Color.BLUE, 9)

        val result = driver.submit(
            ActivateAbility(
                playerId = me, sourceId = jewel, abilityId = craftAbilityId,
                costPayment = AdditionalCostPayment(exiledCards = valid + vanilla)
            )
        )
        result.isSuccess shouldBe false
        // Nothing moved.
        driver.state.getZone(ZoneKey(me, Zone.BATTLEFIELD)).contains(vanilla) shouldBe true
        driver.state.getEntity(jewel)?.get<CardComponent>()?.name shouldBe "The Enigma Jewel"
    }

    test("a land with an activated ability is not valid craft material (must be nonland)") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.putPermanentOnBattlefield(me, "The Enigma Jewel")
        val valid = listOf("Enigma Cache A", "Enigma Cache B", "Enigma Cache C")
            .map { driver.putPermanentOnBattlefield(me, it) }
        val land = driver.putPermanentOnBattlefield(me, "Enigma Ability Land")
        driver.giveMana(me, Color.BLUE, 9)

        val result = driver.submit(
            ActivateAbility(
                playerId = me, sourceId = jewel, abilityId = craftAbilityId,
                costPayment = AdditionalCostPayment(exiledCards = valid + land)
            )
        )
        result.isSuccess shouldBe false
        driver.state.getZone(ZoneKey(me, Zone.BATTLEFIELD)).contains(land) shouldBe true
    }

    test("craft requires at least four materials (fewer is rejected)") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.putPermanentOnBattlefield(me, "The Enigma Jewel")
        val three = listOf("Enigma Cache A", "Enigma Cache B", "Enigma Cache C")
            .map { driver.putPermanentOnBattlefield(me, it) }
        driver.giveMana(me, Color.BLUE, 9)

        val result = driver.submit(
            ActivateAbility(
                playerId = me, sourceId = jewel, abilityId = craftAbilityId,
                costPayment = AdditionalCostPayment(exiledCards = three)
            )
        )
        result.isSuccess shouldBe false
        driver.state.getEntity(jewel)?.get<CardComponent>()?.name shouldBe "The Enigma Jewel"
    }

    test("a card in the graveyard with an activated ability is valid craft material") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.putPermanentOnBattlefield(me, "The Enigma Jewel")
        val onField = listOf("Enigma Cache A", "Enigma Cache B", "Enigma Cache C")
            .map { driver.putPermanentOnBattlefield(me, it) }
        val fromYard = driver.putCardInGraveyard(me, "Enigma Cache D")

        driver.craftJewel(me, jewel, onField + fromYard)

        driver.state.getEntity(jewel)?.get<CardComponent>()?.name shouldBe "Locus of Enlightenment"
        driver.state.getZone(ZoneKey(me, Zone.EXILE)).contains(fromYard) shouldBe true
    }

    test("a graveyard card whose only activated ability is graveyard-activated is valid craft material") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.putPermanentOnBattlefield(me, "The Enigma Jewel")
        val onField = listOf("Enigma Cache A", "Enigma Cache B", "Enigma Cache C")
            .map { driver.putPermanentOnBattlefield(me, it) }
        // Its sole activated ability functions from the graveyard, never the battlefield — it must
        // still count as "a nonland with an activated ability" (the material pool spans the graveyard).
        val fromYard = driver.putCardInGraveyard(me, "Enigma Graveyard Cache")

        driver.craftJewel(me, jewel, onField + fromYard)

        driver.state.getEntity(jewel)?.get<CardComponent>()?.name shouldBe "Locus of Enlightenment"
        driver.state.getZone(ZoneKey(me, Zone.EXILE)).contains(fromYard) shouldBe true
    }

    // =====================================================================================
    // Back face — Locus of Enlightenment: granted abilities
    // =====================================================================================

    test("the Locus has each activated ability of the exiled cards used to craft it — {T} taps the Locus") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.putPermanentOnBattlefield(me, "The Enigma Jewel")
        val materials = listOf("Enigma Tap Cache", "Enigma Cache B", "Enigma Cache C", "Enigma Cache D")
            .map { driver.putPermanentOnBattlefield(me, it) }
        val locus = driver.craftJewel(me, jewel, materials)
        driver.giveMana(me, Color.BLUE, 5) // so every granted ability is affordable when enumerated

        // The Locus offers one activation per exiled card (4 granted abilities).
        driver.abilitiesOn(me, locus) shouldHaveSize 4

        // Activate the granted "{T}: You gain 1 life" (from the Enigma Tap Cache material, materials[0]).
        // Its {T} must tap the Locus itself (the ability's source binds to the Locus, CR 707.10b), and
        // it gains life (doubled by the copy clause).
        val lifeBefore = driver.getLifeTotal(me)
        driver.submitSuccess(driver.abilitiesOn(me, locus).first { it.abilityId.value.contains(fromMaterial(materials[0])) })
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard < 20) { driver.bothPass(); guard++ }

        driver.state.getEntity(locus)!!.has<TappedComponent>() shouldBe true
        // Original + copy each gain 1 life.
        driver.getLifeTotal(me) shouldBe lifeBefore + 2
    }

    test("each granted ability may be activated only once each turn, and refreshes next turn") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.putPermanentOnBattlefield(me, "The Enigma Jewel")
        val materials = listOf("Enigma Cache A", "Enigma Cache B", "Enigma Cache C", "Enigma Cache D")
            .map { driver.putPermanentOnBattlefield(me, it) }
        val locus = driver.craftJewel(me, jewel, materials)

        // Activate the "Enigma Cache A" granted ability (gain 1 life).
        driver.giveMana(me, Color.BLUE, 1)
        val cacheAAbility = driver.abilitiesOn(me, locus).first { it.abilityId.value.contains(fromMaterial(materials[0])) }
        driver.submitSuccess(cacheAAbility)
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard < 20) { driver.bothPass(); guard++ }

        // It is spent for the turn: not offered even with mana in pool, and re-submitting is rejected.
        driver.giveMana(me, Color.BLUE, 2)
        driver.abilitiesOn(me, locus).any { it.abilityId == cacheAAbility.abilityId } shouldBe false
        driver.submit(cacheAAbility).isSuccess shouldBe false

        // Advance to my next turn (opponent's turn, then mine) — the cap resets at cleanup. Route
        // through the end step each time, since passPriorityUntil is a no-op if already at the target.
        driver.passPriorityUntil(Step.END)             // turn 1 end
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)  // opponent's turn
        driver.passPriorityUntil(Step.END)             // opponent's end
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)  // my next turn
        driver.activePlayer shouldBe me
        driver.giveMana(me, Color.BLUE, 1)
        driver.abilitiesOn(me, locus).any { it.abilityId == cacheAAbility.abilityId } shouldBe true
    }

    test("two exiled copies of the same card each get their own once-each-turn budget") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val jewel = driver.putPermanentOnBattlefield(me, "The Enigma Jewel")
        // Two copies of Enigma Cache A + two others.
        val copy1 = driver.putPermanentOnBattlefield(me, "Enigma Cache A")
        val copy2 = driver.putPermanentOnBattlefield(me, "Enigma Cache A")
        val other = listOf("Enigma Cache B", "Enigma Cache C").map { driver.putPermanentOnBattlefield(me, it) }
        val locus = driver.craftJewel(me, jewel, listOf(copy1, copy2) + other)
        driver.giveMana(me, Color.BLUE, 5)

        // Both copies contribute a *distinct* granted "gain 1 life" ability.
        val gainOnes = driver.abilitiesOn(me, locus).filter {
            it.abilityId.value.contains(fromMaterial(copy1)) || it.abilityId.value.contains(fromMaterial(copy2))
        }
        gainOnes shouldHaveSize 2

        // Activate the first; the second (from the other exiled copy) stays available the same turn.
        driver.submitSuccess(gainOnes.first { it.abilityId.value.contains(fromMaterial(copy1)) })
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard < 20) { driver.bothPass(); guard++ }

        driver.giveMana(me, Color.BLUE, 5)
        driver.abilitiesOn(me, locus).any { it.abilityId.value.contains(fromMaterial(copy2)) } shouldBe true
    }

    // =====================================================================================
    // Back face — Locus of Enlightenment: copy clause
    // =====================================================================================

    test("Locus of Enlightenment copies a targeted activated ability and new targets may be chosen") {
        val driver = createDriver()
        val me = driver.player1

        driver.putPermanentOnBattlefield(me, "Locus of Enlightenment")
        val pumper = driver.putCreatureOnBattlefield(me, "Test Pumper")
        driver.removeSummoningSickness(pumper)

        val creatureA = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val creatureB = driver.putCreatureOnBattlefield(me, "Grizzly Bears")

        val pumpAbilityId = driver.cardRegistry.requireCard("Test Pumper").activatedAbilities[0].id
        driver.submitSuccess(
            ActivateAbility(
                playerId = me, sourceId = pumper, abilityId = pumpAbilityId,
                targets = listOf(ChosenTarget.Permanent(creatureA))
            )
        )

        var guard = 0
        while (driver.state.pendingDecision !is ChooseTargetsDecision && guard < 20) {
            driver.bothPass(); guard++
        }
        (driver.state.pendingDecision is ChooseTargetsDecision) shouldBe true
        driver.submitTargetSelection(me, listOf(creatureB)).isSuccess shouldBe true

        guard = 0
        while (driver.state.stack.isNotEmpty() && guard < 20) { driver.bothPass(); guard++ }

        driver.state.projectedState.getPower(creatureA) shouldBe 3
        driver.state.projectedState.getPower(creatureB) shouldBe 3
    }

    test("Locus of Enlightenment copies an untargeted activated ability without a prompt") {
        val driver = createDriver()
        val me = driver.player1

        driver.putPermanentOnBattlefield(me, "Locus of Enlightenment")
        val cleric = driver.putCreatureOnBattlefield(me, "Test Lifegainer")
        driver.removeSummoningSickness(cleric)

        val lifeBefore = driver.getLifeTotal(me)
        val gainAbilityId = driver.cardRegistry.requireCard("Test Lifegainer").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = cleric, abilityId = gainAbilityId))

        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard < 20) {
            (driver.state.pendingDecision is ChooseTargetsDecision) shouldBe false
            driver.bothPass(); guard++
        }

        driver.getLifeTotal(me) shouldBe lifeBefore + 2
    }

    test("mana abilities do not fire the copy trigger (CR 605.1a)") {
        val driver = createDriver()
        val me = driver.player1

        driver.putPermanentOnBattlefield(me, "Locus of Enlightenment")
        driver.tapJewelForRestrictedMana(me)

        driver.state.stack.isEmpty() shouldBe true
        (driver.state.pendingDecision == null) shouldBe true
        driver.state.getEntity(me)?.get<ManaPoolComponent>()!!.restrictedMana.size shouldBe 2
    }
})
