package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.DireFlail
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Dire Flail // Dire Blunderbuss (LCI #145) — {R} Artifact — Equipment // Artifact — Equipment.
 *
 * Front: Equipped creature gets +2/+0; Equip {1};
 *        Craft with artifact {3}{R}{R} (exactly one artifact material, CR 702.167).
 * Back:  Equipped creature gets +3/+0 and has "Whenever this creature attacks, you may
 *        sacrifice an artifact other than Dire Blunderbuss. When you do, this creature
 *        deals damage equal to its power to target creature."; Equip {1}.
 *
 * Covers:
 *  - Front: Equip {1} attaches and grants +2/+0.
 *  - Craft end-to-end: pays {3}{R}{R}, exiles self + exactly one artifact, returns
 *    transformed as Dire Blunderbuss — which (unlike Sovereign's Macuahuitl) has no ETB
 *    attach trigger and enters unattached; its Equip {1} then grants +3/+0.
 *  - Granted attack trigger happy path: equipped 3/3 (→ 6/3) attacks, controller accepts
 *    the optional sacrifice, sacrifices another artifact, and the reflexive part deals
 *    damage equal to the creature's buffed power (6) to a target 2/4 (3 unbuffed damage
 *    would not have killed it).
 *  - Declining the optional sacrifice: no damage, the artifact stays.
 *  - Exclusion: with no artifact other than Dire Blunderbuss itself, the attack presents
 *    no sacrifice prompt at all (the Blunderbuss can't be its own fodder).
 *  - Negative: craft rejected when the supplied material is a creature, not an artifact.
 */
class DireFlailScenarioTest : FunSpec({

    // Plain artifact — craft material / sacrifice fodder.
    val trinket = card("Test Flail Trinket") {
        manaCost = "{1}"
        typeLine = "Artifact"
        oracleText = ""
    }

    // 2/4 victim: dies to the buffed 6 damage, but would survive the unbuffed 3 —
    // proves the +3/+0 is part of the reflexive damage.
    val ox = CardDefinition.creature(
        name = "Test Sturdy Ox",
        manaCost = ManaCost.parse("{3}{G}"),
        subtypes = setOf(Subtype("Ox")),
        power = 2,
        toughness = 4,
        oracleText = ""
    )

    // 0/20 wall: a durable reflexive-damage target that survives repeated hits, so a scenario
    // with two attack triggers always has a legal creature to point the "deals damage" at.
    val wall = CardDefinition.creature(
        name = "Test Big Wall",
        manaCost = ManaCost.parse("{4}"),
        subtypes = setOf(Subtype("Wall")),
        power = 0,
        toughness = 20,
        oracleText = ""
    )

    val projector = StateProjector()

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(trinket, ox, wall))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        return driver
    }

    // Front face: Equip {1} + the Craft — distinguish by the equip marker.
    fun craftAbilityId() = DireFlail.activatedAbilities.single { !it.isEquipAbility }.id
    fun frontEquipAbilityId() = DireFlail.activatedAbilities.single { it.isEquipAbility }.id

    // Back face's only activated ability is Equip {1}.
    fun backEquipAbilityId() = DireFlail.backFace!!.activatedAbilities.single().id

    fun GameTestDriver.drainStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && pendingDecision == null && guard++ < 20) bothPass()
    }

    fun GameTestDriver.resolveUntilDecision() {
        var guard = 0
        while (pendingDecision == null && guard++ < 20) bothPass()
        pendingDecision.shouldNotBeNull()
    }

    /**
     * Shared craft setup: Dire Flail + one trinket material on p1's battlefield; crafts in
     * the precombat main. The back face enters unattached (no ETB trigger). Returns the
     * (now transformed) Equipment's entity id.
     */
    fun GameTestDriver.craftToBlunderbuss(p1: EntityId): EntityId {
        val flail = putPermanentOnBattlefield(p1, "Dire Flail")
        val material = putPermanentOnBattlefield(p1, "Test Flail Trinket")
        passPriorityUntil(Step.PRECOMBAT_MAIN)
        giveMana(p1, Color.RED, 5)

        submit(
            ActivateAbility(
                playerId = p1,
                sourceId = flail,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(material))
            )
        ).isSuccess shouldBe true
        drainStack()

        state.getZone(ZoneKey(p1, Zone.EXILE)).shouldContain(material)
        return flail
    }

    /** Equip the (back-face) Blunderbuss to [creature] for {1} at sorcery speed. */
    fun GameTestDriver.equipBlunderbuss(p1: EntityId, blunderbuss: EntityId, creature: EntityId) {
        giveColorlessMana(p1, 1)
        submit(
            ActivateAbility(
                playerId = p1,
                sourceId = blunderbuss,
                abilityId = backEquipAbilityId(),
                targets = listOf(ChosenTarget.Permanent(creature))
            )
        ).isSuccess shouldBe true
        bothPass()
        state.getEntity(blunderbuss)?.get<AttachedToComponent>()?.targetId shouldBe creature
    }

    test("front face: equip {1} attaches Dire Flail and the equipped creature gets +2/+0") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val flail = driver.putPermanentOnBattlefield(p1, "Dire Flail")
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser") // 3/3
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveColorlessMana(p1, 1)

        driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = flail,
                abilityId = frontEquipAbilityId(),
                targets = listOf(ChosenTarget.Permanent(courser))
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.state.getEntity(flail)?.get<AttachedToComponent>()?.targetId shouldBe courser
        projector.getProjectedPower(driver.state, courser) shouldBe 5
        projector.getProjectedToughness(driver.state, courser) shouldBe 3
    }

    test("craft with artifact: exiles self + one artifact, returns as Dire Blunderbuss unattached; equip {1} grants +3/+0") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val blunderbuss = driver.craftToBlunderbuss(p1)

        // Back face on the battlefield: an Artifact — Equipment named Dire Blunderbuss.
        val container = driver.state.getEntity(blunderbuss)
        container.shouldNotBeNull()
        val card = container.get<CardComponent>()
        card.shouldNotBeNull()
        card.name shouldBe "Dire Blunderbuss"
        card.typeLine.cardTypes shouldBe setOf(CardType.ARTIFACT)
        card.typeLine.subtypes.shouldContain(Subtype.EQUIPMENT)

        val dfc = container.get<DoubleFacedComponent>()
        dfc.shouldNotBeNull()
        dfc.currentFace shouldBe DoubleFacedComponent.Face.BACK

        // No ETB attach trigger (unlike Sovereign's Macuahuitl): enters unattached, no
        // pending decision.
        container.get<AttachedToComponent>() shouldBe null
        driver.pendingDecision shouldBe null

        // Equip {1}: the equipped creature gets +3/+0.
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser") // 3/3
        driver.equipBlunderbuss(p1, blunderbuss, courser)
        projector.getProjectedPower(driver.state, courser) shouldBe 6
        projector.getProjectedToughness(driver.state, courser) shouldBe 3
    }

    test("granted attack trigger: sacrifice another artifact, the equipped creature deals its (buffed) power to target creature") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val opponent = driver.getOpponent(p1)

        val blunderbuss = driver.craftToBlunderbuss(p1)
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser") // 3/3 → 6/3
        driver.removeSummoningSickness(courser)
        driver.equipBlunderbuss(p1, blunderbuss, courser)
        val fodder = driver.putPermanentOnBattlefield(p1, "Test Flail Trinket")
        val victim = driver.putCreatureOnBattlefield(opponent, "Test Sturdy Ox") // 2/4

        projector.getProjectedPower(driver.state, courser) shouldBe 6

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(courser), opponent).error shouldBe null

        // Resolve the granted attack trigger: "You may sacrifice an artifact other than
        // Dire Blunderbuss. When you do, ... target creature". Granted optional TARGETED
        // triggers run the may-then-target flow (may first, reflexive target at placement,
        // sacrifice selection at resolution) — answer decisions by shape, not by a fixed
        // order, so the test pins semantics rather than sequencing.
        driver.resolveUntilDecision()
        var guard = 0
        while (driver.pendingDecision != null && guard++ < 10) {
            when (val d = driver.pendingDecision) {
                is YesNoDecision -> driver.submitYesNo(p1, true).error shouldBe null
                is ChooseTargetsDecision -> {
                    val legal = d.legalTargets[0].orEmpty()
                    val pick = if (victim in legal) victim else fodder
                    driver.submitTargetSelection(p1, listOf(pick)).error shouldBe null
                }
                is SelectCardsDecision -> {
                    val pick = if (fodder in d.options) fodder else victim
                    driver.submitCardSelection(p1, listOf(pick))
                }
                else -> break
            }
            if (driver.pendingDecision == null) driver.drainStack()
        }

        // Fodder sacrificed; 6 damage killed the 2/4 (unbuffed 3 would not have).
        driver.getGraveyard(p1).shouldContain(fodder)
        driver.findPermanent(opponent, "Test Sturdy Ox") shouldBe null
        // The Blunderbuss itself was NOT sacrificed.
        driver.state.getEntity(blunderbuss)?.get<AttachedToComponent>()?.targetId shouldBe courser
    }

    test("declining the optional sacrifice: no damage, the artifact stays") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val opponent = driver.getOpponent(p1)

        val blunderbuss = driver.craftToBlunderbuss(p1)
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser")
        driver.removeSummoningSickness(courser)
        driver.equipBlunderbuss(p1, blunderbuss, courser)
        val fodder = driver.putPermanentOnBattlefield(p1, "Test Flail Trinket")
        val victim = driver.putCreatureOnBattlefield(opponent, "Test Sturdy Ox")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(courser), opponent).error shouldBe null

        driver.resolveUntilDecision()
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(p1, false).error shouldBe null
        driver.drainStack()

        // Nothing happened: fodder still on the battlefield, victim untouched.
        driver.findPermanent(p1, "Test Flail Trinket") shouldNotBe null
        driver.getGraveyard(p1).contains(fodder) shouldBe false
        driver.findPermanent(opponent, "Test Sturdy Ox") shouldBe victim
    }

    test("exclusion: no sacrifice prompt when the only artifact you control is Dire Blunderbuss itself") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val opponent = driver.getOpponent(p1)

        val blunderbuss = driver.craftToBlunderbuss(p1)
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser")
        driver.removeSummoningSickness(courser)
        driver.equipBlunderbuss(p1, blunderbuss, courser)
        val victim = driver.putCreatureOnBattlefield(opponent, "Test Sturdy Ox")
        // No other artifact: the craft material is in exile, and the Blunderbuss is
        // excluded ("an artifact other than Dire Blunderbuss").

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(courser), opponent).error shouldBe null
        driver.drainStack()

        // The optional sacrifice is infeasible — no prompt, no damage, Equipment intact.
        driver.pendingDecision shouldBe null
        driver.findPermanent(opponent, "Test Sturdy Ox") shouldBe victim
        driver.state.getEntity(blunderbuss)?.get<AttachedToComponent>()?.targetId shouldBe courser
    }

    test("a second Dire Blunderbuss (unattached) is a legal sacrifice; only the granting one is excluded") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val opponent = driver.getOpponent(p1)

        // Two Blunderbusses: b1 will be equipped (the granting Equipment), b2 left unattached.
        val b1 = driver.craftToBlunderbuss(p1)
        val b2 = driver.craftToBlunderbuss(p1)
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser") // 3/3 → 6/3
        driver.removeSummoningSickness(courser)
        driver.equipBlunderbuss(p1, b1, courser)
        val victim = driver.putCreatureOnBattlefield(opponent, "Test Sturdy Ox") // 2/4

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(courser), opponent).error shouldBe null

        // Under the old `notNamed("Dire Blunderbuss")` filter both copies were excluded, leaving
        // no fodder — so no sacrifice, no damage. With `.notGrantingPermanent()` only the specific
        // granting copy (b1) is excluded; the second copy b2 is legal fodder.
        driver.resolveUntilDecision()
        var guard = 0
        while (driver.pendingDecision != null && guard++ < 10) {
            when (val d = driver.pendingDecision) {
                is YesNoDecision -> driver.submitYesNo(p1, true).error shouldBe null
                is ChooseTargetsDecision -> {
                    val legal = d.legalTargets[0].orEmpty()
                    when {
                        b2 in legal || b1 in legal ->
                            driver.submitTargetSelection(p1, listOf(b2)).error shouldBe null
                        else -> driver.submitTargetSelection(p1, listOf(victim)).error shouldBe null
                    }
                }
                is SelectCardsDecision -> driver.submitCardSelection(p1, listOf(b2))
                else -> break
            }
            if (driver.pendingDecision == null) driver.drainStack()
        }

        // The unattached second Blunderbuss was sacrificed; the attached granting one is intact;
        // 6 damage killed the 2/4.
        driver.getGraveyard(p1).shouldContain(b2)
        driver.state.getEntity(b1)?.get<AttachedToComponent>()?.targetId shouldBe courser
        driver.findPermanent(opponent, "Test Sturdy Ox") shouldBe null
    }

    test("only the granter is excluded: another Equipment co-attached to the same creature is still sacrificable") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val opponent = driver.getOpponent(p1)

        val blunderbuss = driver.craftToBlunderbuss(p1) // the granter
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser")
        driver.removeSummoningSickness(courser)
        driver.equipBlunderbuss(p1, blunderbuss, courser)

        // A second Equipment (a Dire Flail front face) also equipped to the SAME creature.
        // Under the old `.notAttachedToSource()` filter it was wrongly excluded (attached to the
        // source); `.notGrantingPermanent()` excludes only the granting Blunderbuss, so it's legal.
        val coEquip = driver.putPermanentOnBattlefield(p1, "Dire Flail")
        driver.giveColorlessMana(p1, 1)
        driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = coEquip,
                abilityId = frontEquipAbilityId(),
                targets = listOf(ChosenTarget.Permanent(courser))
            )
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getEntity(coEquip)?.get<AttachedToComponent>()?.targetId shouldBe courser

        val victim = driver.putCreatureOnBattlefield(opponent, "Test Sturdy Ox") // 2/4

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(courser), opponent).error shouldBe null

        driver.resolveUntilDecision()
        var guard = 0
        while (driver.pendingDecision != null && guard++ < 10) {
            when (val d = driver.pendingDecision) {
                is YesNoDecision -> driver.submitYesNo(p1, true).error shouldBe null
                is ChooseTargetsDecision -> {
                    val legal = d.legalTargets[0].orEmpty()
                    when {
                        coEquip in legal || blunderbuss in legal ->
                            driver.submitTargetSelection(p1, listOf(coEquip)).error shouldBe null
                        else -> driver.submitTargetSelection(p1, listOf(victim)).error shouldBe null
                    }
                }
                is SelectCardsDecision -> driver.submitCardSelection(p1, listOf(coEquip))
                else -> break
            }
            if (driver.pendingDecision == null) driver.drainStack()
        }

        // The co-attached Dire Flail (not the granter) was sacrificed; the granting Blunderbuss is intact.
        driver.getGraveyard(p1).shouldContain(coEquip)
        driver.state.getEntity(blunderbuss)?.get<AttachedToComponent>()?.targetId shouldBe courser
    }

    test("two identical granters on one creature are told apart: each excludes only itself, so both can be sacrificed") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val opponent = driver.getOpponent(p1)

        // Two Dire Blunderbusses, both equipped to the same creature. No other artifact exists.
        val b1 = driver.craftToBlunderbuss(p1)
        val b2 = driver.craftToBlunderbuss(p1)
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser")
        driver.removeSummoningSickness(courser)
        driver.equipBlunderbuss(p1, b1, courser)
        driver.equipBlunderbuss(p1, b2, courser)

        // Durable target: the equipped 3/3 is 9/3 with two Blunderbusses, so the reflexive damage
        // would kill a small creature and leave the second trigger no legal creature target. A 0/20
        // wall survives both hits, keeping the test focused on the sacrifice-exclusion behavior.
        val victim = driver.putCreatureOnBattlefield(opponent, "Test Big Wall") // 0/20

        // Both grants fire an attack trigger (the ability isn't once-per-turn). Each grant excludes
        // ONLY its own granter, so b1's trigger can sacrifice b2 and b2's trigger can sacrifice b1.
        // Under the old first-match `granterId` derivation both triggers pinned the granter to
        // whichever copy sorted first, so after the first sacrifice the second trigger saw no legal
        // fodder — proving the per-(source, ability) cursor now maps the two triggers 1:1 onto the
        // two granters.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(courser), opponent).error shouldBe null

        driver.resolveUntilDecision()
        var guard = 0
        while (driver.pendingDecision != null && guard++ < 20) {
            when (val d = driver.pendingDecision) {
                is YesNoDecision -> driver.submitYesNo(p1, true).error shouldBe null
                is ChooseTargetsDecision -> {
                    val legal = d.legalTargets[0].orEmpty()
                    // A Blunderbuss in the legal set means this is the sacrifice-target choice
                    // (the other copy); otherwise it's the reflexive damage's creature target.
                    val pick = when {
                        b1 in legal -> b1
                        b2 in legal -> b2
                        else -> victim
                    }
                    driver.submitTargetSelection(p1, listOf(pick)).error shouldBe null
                }
                is SelectCardsDecision -> {
                    val pick = d.options.firstOrNull { it == b1 || it == b2 } ?: d.options.first()
                    driver.submitCardSelection(p1, listOf(pick))
                }
                else -> break
            }
            if (driver.pendingDecision == null) driver.drainStack()
        }

        // Both Blunderbusses ended up sacrificed — each trigger found the *other* copy as fodder.
        driver.getGraveyard(p1).shouldContain(b1)
        driver.getGraveyard(p1).shouldContain(b2)
    }

    test("negative: craft rejected when the supplied material is a creature, not an artifact") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val flail = driver.putPermanentOnBattlefield(p1, "Dire Flail")
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser") // not an artifact
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.RED, 5)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = flail,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(courser))
            )
        )
        result.isSuccess shouldBe false

        // Nothing moved: still the front face, the Courser untouched.
        driver.state.getEntity(flail)!!.get<CardComponent>()!!.name shouldBe "Dire Flail"
        driver.findPermanent(p1, "Centaur Courser") shouldNotBe null
    }
})
