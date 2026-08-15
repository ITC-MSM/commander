package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CraftedFromExiledComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.ThroneOfTheGrimCaptain
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.effects.ReturnSelfFromExileTransformedEffect
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain as stringShouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Throne of the Grim Captain // The Grim Captain (LCI #266).
 *
 * Front face — Throne of the Grim Captain ({2}, Legendary Artifact):
 *   "{T}: Mill two cards.
 *    Craft with a Dinosaur, a Merfolk, a Pirate, and a Vampire {4}"
 * Back face — The Grim Captain (Legendary Creature — Skeleton Spirit Pirate, 7/7):
 *   "Menace, trample, lifelink, hexproof
 *    Whenever The Grim Captain attacks, each opponent sacrifices a nonland permanent of
 *    their choice. Then you may put an exiled creature card used to craft The Grim Captain
 *    onto the battlefield under your control tapped and attacking."
 *
 * Covers the whole card, including the two engine features it needed:
 *  - Heterogeneous slot-based Craft (one of each of four subtypes) with bipartite-matching
 *    validation — a per-subtype count check would wrongly accept four Vampires.
 *  - The attack trigger's second sentence via `CardSource.CraftedMaterials` +
 *    `ZonePlacement.TappedAndAttacking`.
 */
class ThroneOfTheGrimCaptainScenarioTest : FunSpec({

    // Material creatures, one per named craft subtype, plus a dual-type Merfolk Pirate and
    // spare Vampires to exercise the bipartite matching.
    val raptor = card("Test Raptor") { manaCost = "{1}"; typeLine = "Creature — Dinosaur"; power = 1; toughness = 1 }
    val merfolk = card("Test Merfolk") { manaCost = "{1}"; typeLine = "Creature — Merfolk"; power = 1; toughness = 1 }
    val pirate = card("Test Pirate") { manaCost = "{1}"; typeLine = "Creature — Pirate"; power = 1; toughness = 1 }
    val vampire = card("Test Vampire") { manaCost = "{1}"; typeLine = "Creature — Vampire"; power = 1; toughness = 1 }
    val merfolkPirate = card("Test Merfolk Pirate") { manaCost = "{1}"; typeLine = "Creature — Merfolk Pirate"; power = 1; toughness = 1 }
    // Nonland permanents for the opponent to feed to the edict.
    val bears = card("Test Bears") { manaCost = "{1}{G}"; typeLine = "Creature — Bear"; power = 2; toughness = 2 }
    val trinket = card("Test Trinket") { manaCost = "{1}"; typeLine = "Artifact" }

    val materialCards = listOf(raptor, merfolk, pirate, vampire, merfolkPirate, bears, trinket)

    val projector = StateProjector()

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + materialCards)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        return driver
    }

    // Throne's front face carries two activated abilities — the mill and the craft. The craft is
    // the one whose effect returns the source from exile transformed.
    fun craftAbilityId() = ThroneOfTheGrimCaptain.activatedAbilities
        .first { it.effect === ReturnSelfFromExileTransformedEffect }.id
    fun millAbilityId() = ThroneOfTheGrimCaptain.activatedAbilities
        .first { it.effect !== ReturnSelfFromExileTransformedEffect }.id

    /** Craft the Throne with one of each named subtype; returns the four exiled material ids. */
    fun GameTestDriver.craftGrimCaptain(p1: EntityId, throne: EntityId): List<EntityId> {
        val dino = putCreatureOnBattlefield(p1, "Test Raptor")
        val merf = putCreatureOnBattlefield(p1, "Test Merfolk")
        val pir = putCreatureOnBattlefield(p1, "Test Pirate")
        val vamp = putCreatureOnBattlefield(p1, "Test Vampire")
        giveMana(p1, Color.BLACK, 4)
        submitSuccess(
            ActivateAbility(
                playerId = p1, sourceId = throne, abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(dino, merf, pir, vamp))
            )
        )
        bothPass()
        state.getEntity(throne)!!.get<CardComponent>()!!.name shouldBe "The Grim Captain"
        return listOf(dino, merf, pir, vamp)
    }

    test("front face: {T}: Mill two cards puts the top two library cards into the graveyard") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val throne = driver.putPermanentOnBattlefield(p1, "Throne of the Grim Captain")
        driver.putCardOnTopOfLibrary(p1, "Hill Giant")
        driver.putCardOnTopOfLibrary(p1, "Grizzly Bears")

        driver.submitSuccess(
            ActivateAbility(playerId = p1, sourceId = throne, abilityId = millAbilityId())
        )
        driver.bothPass()

        driver.isTapped(throne) shouldBe true
        driver.getGraveyardCardNames(p1).shouldContainExactlyInAnyOrder("Grizzly Bears", "Hill Giant")
    }

    test("craft: exiling one of each named subtype transforms into The Grim Captain and records the materials") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val throne = driver.putPermanentOnBattlefield(p1, "Throne of the Grim Captain")
        val materials = driver.craftGrimCaptain(p1, throne)

        // Back-face identity.
        val container = driver.state.getEntity(throne).shouldNotBeNull()
        val cardComp = container.get<CardComponent>().shouldNotBeNull()
        cardComp.name shouldBe "The Grim Captain"
        cardComp.typeLine.cardTypes shouldBe setOf(CardType.CREATURE)
        cardComp.typeLine.supertypes shouldBe setOf(Supertype.LEGENDARY)
        cardComp.typeLine.subtypes shouldBe setOf(Subtype.SKELETON, Subtype.SPIRIT, Subtype.PIRATE)
        container.get<DoubleFacedComponent>()!!.currentFace shouldBe DoubleFacedComponent.Face.BACK

        val projected = projector.project(driver.state)
        projected.getPower(throne) shouldBe 7
        projected.getToughness(throne) shouldBe 7
        projected.hasKeyword(throne, Keyword.MENACE) shouldBe true
        projected.hasKeyword(throne, Keyword.TRAMPLE) shouldBe true
        projected.hasKeyword(throne, Keyword.LIFELINK) shouldBe true
        projected.hasKeyword(throne, Keyword.HEXPROOF) shouldBe true
        // The back face's black color indicator (CR 204) makes the transformed permanent black,
        // even though it has no mana cost.
        projected.hasColor(throne, Color.BLACK) shouldBe true
        projected.getColors(throne) shouldBe setOf(Color.BLACK.name)

        // Materials exiled and recorded on the returned permanent.
        materials.forEach { driver.getExile(p1).shouldContain(it) }
        val crafted = container.get<CraftedFromExiledComponent>().shouldNotBeNull()
        crafted.exiledIds.shouldContainExactlyInAnyOrder(materials)
    }

    test("craft: a dual-type Merfolk Pirate can fill either the Merfolk or the Pirate slot") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val throne = driver.putPermanentOnBattlefield(p1, "Throne of the Grim Captain")
        val dino = driver.putCreatureOnBattlefield(p1, "Test Raptor")
        val pir = driver.putCreatureOnBattlefield(p1, "Test Pirate")
        val vamp = driver.putCreatureOnBattlefield(p1, "Test Vampire")
        // The Merfolk Pirate fills the Merfolk slot; the real Pirate fills the Pirate slot.
        val merfPir = driver.putCreatureOnBattlefield(p1, "Test Merfolk Pirate")
        driver.giveMana(p1, Color.BLACK, 4)

        driver.submitSuccess(
            ActivateAbility(
                playerId = p1, sourceId = throne, abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(dino, merfPir, pir, vamp))
            )
        )
        driver.bothPass()

        driver.state.getEntity(throne)!!.get<CardComponent>()!!.name shouldBe "The Grim Captain"
    }

    test("craft is not activatable when the pool cannot cover every slot (four Vampires — count alone would accept)") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val throne = driver.putPermanentOnBattlefield(p1, "Throne of the Grim Captain")
        val vamps = (1..4).map { driver.putCreatureOnBattlefield(p1, "Test Vampire") }
        driver.giveMana(p1, Color.BLACK, 4)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1, sourceId = throne, abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = vamps)
            )
        )
        result.isSuccess shouldBe false

        // Nothing moved — the Throne and all four Vampires stay on the battlefield.
        val battlefield = driver.state.getZone(ZoneKey(p1, Zone.BATTLEFIELD))
        battlefield.contains(throne) shouldBe true
        vamps.forEach { battlefield.contains(it) shouldBe true }
        driver.state.getEntity(throne)!!.get<CardComponent>()!!.name shouldBe "Throne of the Grim Captain"
    }

    test("craft payment rejects a legal-count set that leaves a slot unfilled (two Vampires, no Pirate chosen)") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val throne = driver.putPermanentOnBattlefield(p1, "Throne of the Grim Captain")
        // Pool CAN satisfy all four slots (so the ability is offered), but the chosen subset skips
        // the Pirate and doubles up on Vampires — no matching fills the Pirate slot.
        val dino = driver.putCreatureOnBattlefield(p1, "Test Raptor")
        val merf = driver.putCreatureOnBattlefield(p1, "Test Merfolk")
        driver.putCreatureOnBattlefield(p1, "Test Pirate") // present in pool, deliberately not chosen
        val vamp1 = driver.putCreatureOnBattlefield(p1, "Test Vampire")
        val vamp2 = driver.putCreatureOnBattlefield(p1, "Test Vampire")
        driver.giveMana(p1, Color.BLACK, 4)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1, sourceId = throne, abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(dino, merf, vamp1, vamp2))
            )
        )
        result.isSuccess shouldBe false
        result.error.shouldNotBeNull() stringShouldContain "slot"
        driver.state.getEntity(throne)!!.get<CardComponent>()!!.name shouldBe "Throne of the Grim Captain"
    }

    test("attack trigger: each opponent sacrifices a nonland permanent of their choice") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)

        // Two nonland permanents (so the sacrifice is a real choice offered to the opponent) plus a
        // land, which must NOT be offered.
        val oppBears = driver.putCreatureOnBattlefield(p2, "Test Bears")
        val oppTrinket = driver.putPermanentOnBattlefield(p2, "Test Trinket")
        val oppSwamp = driver.putLandOnBattlefield(p2, "Swamp")
        // The attacker's controller also has a creature — it must NOT be offered.
        val myBears = driver.putCreatureOnBattlefield(p1, "Test Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val throne = driver.putPermanentOnBattlefield(p1, "Throne of the Grim Captain")
        driver.craftGrimCaptain(p1, throne)
        driver.removeSummoningSickness(throne)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(throne), p2).error shouldBe null
        driver.bothPass() // resolve the attack trigger

        val edict = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        withClue("the sacrifice is the opponent's choice among exactly their nonland permanents") {
            edict.playerId shouldBe p2
            edict.options.shouldContainExactlyInAnyOrder(listOf(oppBears, oppTrinket))
        }
        driver.submitCardSelection(p2, listOf(oppTrinket))

        // The Grim Captain also has crafted creatures in exile, so clause 2 offers p1 a choice next;
        // this test focuses on the edict, so decline it.
        (driver.pendingDecision as? SelectCardsDecision)?.let {
            if (it.playerId == p1) driver.submitCardSelection(p1, emptyList())
        }

        driver.getGraveyard(p2).shouldContain(oppTrinket)
        driver.findPermanent(p2, "Test Trinket") shouldBe null
        driver.findPermanent(p2, "Test Bears") shouldBe oppBears
        driver.findPermanent(p2, "Swamp") shouldBe oppSwamp
        driver.findPermanent(p1, "Test Bears") shouldBe myBears
    }

    test("attack trigger clause 2: put an exiled crafted creature card onto the battlefield tapped and attacking") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)

        // Opponent has a single nonland permanent for the edict; with only one legal sacrifice the
        // engine resolves it without prompting, so the edict runs first and clause 2 follows.
        val oppBears = driver.putCreatureOnBattlefield(p2, "Test Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val throne = driver.putPermanentOnBattlefield(p1, "Throne of the Grim Captain")
        val materials = driver.craftGrimCaptain(p1, throne)
        driver.removeSummoningSickness(throne)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(throne), p2).error shouldBe null
        driver.bothPass() // resolve the attack trigger

        // 1) The edict resolves first. If a choice is offered (opponent has >1 nonland permanent),
        //    take it; here the single Test Bears is sacrificed without a prompt.
        (driver.pendingDecision as? SelectCardsDecision)?.takeIf { it.playerId == p2 }?.let {
            driver.submitCardSelection(p2, it.options.take(1))
        }

        // 2) Then "you may put an exiled creature card used to craft ..." — choose the Raptor.
        val put = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        withClue("clause 2 is the attacker-controller's choice among the exiled crafted creatures") {
            put.playerId shouldBe p1
            put.options.shouldContainExactlyInAnyOrder(materials)
        }
        val raptor = materials.first()
        driver.submitCardSelection(p1, listOf(raptor))

        // The Raptor is now on the battlefield under p1's control, tapped and attacking.
        val battlefield = driver.state.getZone(ZoneKey(p1, Zone.BATTLEFIELD))
        battlefield.contains(raptor) shouldBe true
        driver.getExile(p1).shouldNotContain(raptor)
        driver.isTapped(raptor) shouldBe true
        driver.state.getEntity(raptor)!!.get<AttackingComponent>().shouldNotBeNull()

        // The edict still resolved — the opponent's only nonland permanent was sacrificed.
        driver.getGraveyard(p2).shouldContain(oppBears)
    }
})
