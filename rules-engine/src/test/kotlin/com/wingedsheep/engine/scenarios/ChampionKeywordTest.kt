package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChampionedEvent
import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.champion
import com.wingedsheep.sdk.dsl.championCreature
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * The Champion keyword (CR 702.72) end to end: the enters "sacrifice it unless you exile another
 * [object] you control" trigger, the linked leaves-the-battlefield return, and the CR 702.72c
 * "championed" signal that Mistbind Clique's follow-up reads.
 *
 * Uses inline probe cards rather than the printed Lorwyn nine so the rules matrix is independent of
 * any card's other abilities. Each test names the rule it pins.
 */
class ChampionKeywordTest : FunSpec({

    // "Champion a Goblin" — the tribal form (Boggart Mob's shape).
    val goblinChampion = card("Champion Probe Goblin") {
        manaCost = "{3}{B}"
        typeLine = "Creature — Goblin Warrior"
        power = 5
        toughness = 5
        champion(Subtype.GOBLIN)
    }

    // "Champion a creature" — the Changeling form.
    val creatureChampion = card("Champion Probe Creature") {
        manaCost = "{3}{R}"
        typeLine = "Creature — Shapeshifter"
        power = 5
        toughness = 3
        championCreature()
    }

    // Mistbind Clique's shape: champion + the CR 702.72c payoff, with a trivial untargeted effect
    // so the test asserts the trigger fired rather than a targeting flow.
    val payoffChampion = card("Champion Probe Payoff") {
        manaCost = "{3}{U}"
        typeLine = "Creature — Faerie Wizard"
        power = 4
        toughness = 4
        champion(Subtype.FAERIE)
        triggeredAbility {
            trigger = Triggers.championedWith()
            effect = Effects.DrawCards(1)
            description = "When a Faerie is championed with this creature, draw a card."
        }
    }

    val testGoblin = card("Champion Test Goblin") {
        manaCost = "{1}{B}"
        typeLine = "Creature — Goblin"
        power = 2
        toughness = 2
    }

    val testFaerie = card("Champion Test Faerie") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Faerie"
        power = 1
        toughness = 1
    }

    // A *noncreature* Kindred Goblin: champion's printed quality is a bare tribal noun, which per
    // CR 109.2 reads as a Goblin permanent, so this is a legal thing to champion.
    val goblinShrine = card("Champion Test Goblin Shrine") {
        manaCost = "{1}{B}"
        typeLine = "Kindred Enchantment — Goblin"
    }

    val testBear = card("Champion Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    val banish = card("Champion Banish") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.Exile(creature)
        }
    }

    val blink = card("Champion Blink") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.Exile(creature).then(Effects.PutOntoBattlefield(creature))
        }
    }

    val steal = card("Champion Steal") {
        manaCost = "{U}"
        typeLine = "Sorcery"
        spell {
            val creature = target("creature", TargetCreature())
            effect = Effects.GainControl(creature)
        }
    }

    // Makes a non-Goblin into a Goblin, so the candidate gather has to read *projected* types.
    val goblinify = card("Champion Goblinify") {
        manaCost = "{B}"
        typeLine = "Sorcery"
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.AddCreatureType("Goblin", creature)
        }
    }

    val tokenMaker = card("Champion Token Maker") {
        manaCost = "{B}"
        typeLine = "Sorcery"
        spell {
            effect = Effects.CreateToken(
                power = 1, toughness = 1, colors = setOf(Color.BLACK),
                creatureTypes = setOf("Goblin")
            )
        }
    }

    val probes = listOf(
        goblinChampion, creatureChampion, payoffChampion, testGoblin, testFaerie,
        goblinShrine, testBear, banish, blink, steal, goblinify, tokenMaker
    )

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + probes)
        initMirrorMatch(deck = Deck.of("Island" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    /** Drain the stack, answering each champion choice with [selected] (empty list = decline). */
    fun GameTestDriver.resolveChoosing(selected: EntityId? = null) {
        var guard = 0
        while (stackSize > 0 || pendingDecision != null) {
            check(guard++ < 24) { "Resolution did not settle" }
            when (val decision = pendingDecision) {
                null -> bothPass().error shouldBe null
                is SelectCardsDecision -> submitCardSelection(
                    decision.playerId, selected?.let { listOf(it) } ?: emptyList()
                ).error shouldBe null
                is OrderObjectsDecision -> submitDecision(
                    decision.playerId, OrderedResponse(decision.id, decision.objects)
                ).error shouldBe null
                else -> error("Unexpected decision: $decision")
            }
        }
    }

    /** Top up every colour the probe cards use, so a cast never fails on colour. */
    fun GameTestDriver.giveProbeMana(player: EntityId) {
        listOf(Color.BLUE, Color.BLACK, Color.RED, Color.GREEN).forEach { giveMana(player, it, 4) }
    }

    /** Cast [name] from hand for its (mana-cheated) cost and stop at the champion choice. */
    fun GameTestDriver.castChampion(name: String): EntityId {
        val source = putCardInHand(player1, name)
        giveProbeMana(player1)
        castSpell(player1, source).error shouldBe null
        return source
    }

    fun GameTestDriver.graveyardOf(playerId: EntityId): List<EntityId> = state.getGraveyard(playerId)

    /** Pass until the champion ability's choice is on the table, and return it. */
    fun GameTestDriver.passUntilChampionChoice(): SelectCardsDecision {
        var guard = 0
        while (pendingDecision !is SelectCardsDecision) {
            check(guard++ < 8) { "No champion choice was offered" }
            bothPass().error shouldBe null
        }
        return pendingDecision as SelectCardsDecision
    }

    // -------------------------------------------------------------------------
    // CR 702.72a — "sacrifice it unless you exile another [object] you control"
    // -------------------------------------------------------------------------

    test("CR 702.72a: exiling another matching permanent keeps the champion on the battlefield") {
        val d = driver()
        val goblin = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        val source = d.castChampion("Champion Probe Goblin")
        d.resolveChoosing(goblin)

        d.state.getBattlefield() shouldContain source
        d.state.getBattlefield() shouldNotContain goblin
        d.state.getZone(com.wingedsheep.engine.state.ZoneKey(d.player1, Zone.EXILE)) shouldContain goblin
    }

    test("CR 702.72a: declining the choice sacrifices the champion, exiling nothing") {
        val d = driver()
        val goblin = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        val source = d.castChampion("Champion Probe Goblin")
        d.resolveChoosing(selected = null)

        d.state.getBattlefield() shouldNotContain source
        d.graveyardOf(d.player1) shouldContain source
        // The declined permanent is untouched.
        d.state.getBattlefield() shouldContain goblin
    }

    test("CR 702.72a: with no eligible permanent the champion is sacrificed and no choice is offered") {
        val d = driver()
        // A Bear is not a Goblin, so the candidate set is empty.
        d.putCreatureOnBattlefield(d.player1, "Champion Test Bear")
        val source = d.castChampion("Champion Probe Goblin")

        var sawSelection = false
        var guard = 0
        while (d.stackSize > 0 || d.pendingDecision != null) {
            check(guard++ < 16) { "Resolution did not settle" }
            when (val decision = d.pendingDecision) {
                null -> d.bothPass().error shouldBe null
                is SelectCardsDecision -> {
                    sawSelection = true
                    d.submitCardSelection(decision.playerId, emptyList()).error shouldBe null
                }
                else -> error("Unexpected decision: $decision")
            }
        }
        sawSelection shouldBe false
        d.graveyardOf(d.player1) shouldContain source
    }

    test("CR 702.72a: 'another' excludes the champion itself from the candidates") {
        val d = driver()
        val goblin = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        val source = d.castChampion("Champion Probe Goblin")
        val decision = d.passUntilChampionChoice()
        // The champion is itself a Goblin on the battlefield; only the *other* Goblin is offered.
        decision.options shouldContainExactlyInAnyOrder listOf(goblin)
        decision.options shouldNotContain source
    }

    test("CR 702.72a: only permanents you control are candidates") {
        val d = driver()
        val mine = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        d.putCreatureOnBattlefield(d.player2, "Champion Test Goblin")
        d.castChampion("Champion Probe Goblin")
        d.passUntilChampionChoice().options shouldContainExactlyInAnyOrder listOf(mine)
    }

    test("CR 109.2: a bare tribal quality means a permanent, so a noncreature Kindred Goblin qualifies") {
        val d = driver()
        val shrine = d.putPermanentOnBattlefield(d.player1, "Champion Test Goblin Shrine")
        d.castChampion("Champion Probe Goblin")
        d.passUntilChampionChoice().options shouldContain shrine
    }

    test("candidates read projected types: a Bear turned into a Goblin becomes a legal choice") {
        val d = driver()
        val bear = d.putCreatureOnBattlefield(d.player1, "Champion Test Bear")
        val spell = d.putCardInHand(d.player1, "Champion Goblinify")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, spell, listOf(bear)).error shouldBe null
        d.bothPass().error shouldBe null

        d.castChampion("Champion Probe Goblin")
        d.passUntilChampionChoice().options shouldContainExactlyInAnyOrder listOf(bear)
    }

    test("'Champion a creature' offers every creature you control, and no noncreature") {
        val d = driver()
        val bear = d.putCreatureOnBattlefield(d.player1, "Champion Test Bear")
        val goblin = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        d.putPermanentOnBattlefield(d.player1, "Champion Test Goblin Shrine")
        d.castChampion("Champion Probe Creature")
        d.passUntilChampionChoice().options shouldContainExactlyInAnyOrder
            listOf(bear, goblin)
    }

    // -------------------------------------------------------------------------
    // CR 702.72a / 702.72b — the linked leaves-the-battlefield return
    // -------------------------------------------------------------------------

    test("CR 702.72a: the championed card returns when the champion leaves the battlefield") {
        val d = driver()
        val goblin = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        val source = d.castChampion("Champion Probe Goblin")
        d.resolveChoosing(goblin)
        d.state.getBattlefield() shouldNotContain goblin

        val removal = d.putCardInHand(d.player1, "Champion Banish")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
        d.resolveChoosing()

        d.state.getBattlefield() shouldContain goblin
        d.state.getBattlefield() shouldNotContain source
    }

    test("CR 702.72a: the championed card returns under its OWNER's control, not the champion's") {
        val d = driver()
        // player2 owns the Goblin; player1 steals it, then champions it.
        val goblin = d.putCreatureOnBattlefield(d.player2, "Champion Test Goblin")
        val theft = d.putCardInHand(d.player1, "Champion Steal")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, theft, listOf(goblin)).error shouldBe null
        d.bothPass().error shouldBe null
        // Control change is a continuous effect, so read it off projected state.
        d.state.projectedState.getController(goblin) shouldBe d.player1

        val source = d.castChampion("Champion Probe Goblin")
        d.resolveChoosing(goblin)

        val removal = d.putCardInHand(d.player1, "Champion Banish")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
        d.resolveChoosing()

        d.state.getBattlefield() shouldContain goblin
        d.state.projectedState.getController(goblin) shouldBe d.player2
    }

    test("CR 702.72b: the leaves trigger returns only what this champion exiled") {
        val d = driver()
        val championed = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        val bystander = d.putCreatureOnBattlefield(d.player1, "Champion Test Bear")
        val source = d.castChampion("Champion Probe Goblin")
        d.resolveChoosing(championed)

        // Exile the bystander by an unrelated effect — it is not linked to the champion.
        val unrelated = d.putCardInHand(d.player1, "Champion Banish")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, unrelated, listOf(bystander)).error shouldBe null
        d.resolveChoosing()

        val removal = d.putCardInHand(d.player1, "Champion Banish")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
        d.resolveChoosing()

        d.state.getBattlefield() shouldContain championed
        d.state.getBattlefield() shouldNotContain bystander
    }

    test("CR 111.7: a championed token ceases to exist and never returns") {
        val d = driver()
        val maker = d.putCardInHand(d.player1, "Champion Token Maker")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, maker).error shouldBe null
        d.bothPass().error shouldBe null
        val token = d.getCreatures(d.player1).single()

        val source = d.castChampion("Champion Probe Goblin")
        d.resolveChoosing(token)
        d.state.getBattlefield() shouldNotContain token

        val removal = d.putCardInHand(d.player1, "Champion Banish")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
        d.resolveChoosing()

        d.state.getBattlefield() shouldNotContain token
    }

    test("the leaves trigger resolving before the enters trigger exiles a card that never returns") {
        val d = driver()
        val goblin = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        val source = d.castChampion("Champion Probe Goblin")
        // Leave the enters trigger on the stack, then remove the champion: its leaves trigger goes
        // on top and resolves first, against an empty linked-exile pile.
        d.bothPass().error shouldBe null
        d.stackSize shouldBe 1

        val removal = d.putCardInHand(d.player1, "Champion Banish")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
        d.resolveChoosing(goblin)

        d.state.getBattlefield() shouldNotContain source
        // The enters trigger still exiled it — with the return already spent, indefinitely.
        d.state.getBattlefield() shouldNotContain goblin
        d.state.getZone(com.wingedsheep.engine.state.ZoneKey(d.player1, Zone.EXILE)) shouldContain goblin
    }

    test("a champion that blinks does not sacrifice for, or return, the previous visit's card") {
        val d = driver()
        val first = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        val second = d.putCreatureOnBattlefield(d.player1, "Champion Test Goblin")
        val source = d.castChampion("Champion Probe Goblin")
        d.resolveChoosing(first)

        val flicker = d.putCardInHand(d.player1, "Champion Blink")
        d.giveProbeMana(d.player1)
        d.castSpell(d.player1, flicker, listOf(source)).error shouldBe null
        // The old visit's leaves trigger returns `first`; the new visit's enters trigger champions
        // `second`. The new visit is not sacrificed by the old visit's obligations.
        d.resolveChoosing(second)

        d.state.getBattlefield() shouldContain first
        d.state.getBattlefield() shouldNotContain second
        d.state.getBattlefield() shouldContain source
    }

    // -------------------------------------------------------------------------
    // CR 702.72c — "championed"
    // -------------------------------------------------------------------------

    test("CR 702.72c: championing a permanent fires the 'is championed with this creature' payoff") {
        val d = driver()
        val faerie = d.putCreatureOnBattlefield(d.player1, "Champion Test Faerie")
        val handBefore = d.getHand(d.player1).size
        val source = d.castChampion("Champion Probe Payoff")
        d.resolveChoosing(faerie)

        d.events.filterIsInstance<ChampionedEvent>().map { it.championId to it.championedId } shouldBe
            listOf(source to faerie)
        d.getHand(d.player1).size shouldBe handBefore + 1
    }

    test("CR 702.72c: declining champions nothing, so the payoff does not fire") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player1, "Champion Test Faerie")
        val handBefore = d.getHand(d.player1).size
        d.castChampion("Champion Probe Payoff")
        d.resolveChoosing(selected = null)

        d.events.filterIsInstance<ChampionedEvent>() shouldBe emptyList()
        d.getHand(d.player1).size shouldBe handBefore
    }

    test("CR 702.72c: the payoff is bound to its own champion, not to another player's") {
        val d = driver()
        val faerie = d.putCreatureOnBattlefield(d.player1, "Champion Test Faerie")
        val watcher = d.putCreatureOnBattlefield(d.player1, "Champion Probe Payoff")
        val handBefore = d.getHand(d.player1).size

        // A *different* champion does the championing; the watcher's SELF-bound payoff must not fire.
        val source = d.castChampion("Champion Probe Creature")
        d.resolveChoosing(faerie)

        d.events.filterIsInstance<ChampionedEvent>().map { it.championId } shouldBe listOf(source)
        d.getHand(d.player1).size shouldBe handBefore
        // The watcher is untouched; only the chosen Faerie left.
        d.state.getBattlefield() shouldContain watcher
    }
})
