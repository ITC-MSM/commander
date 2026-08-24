package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Mechanic-level tests for Fabricate N (CR 702.123).
 *
 * A fabricate card declares one keyword ability and nothing else; the engine supplies the whole
 * printed ability from [com.wingedsheep.sdk.scripting.Fabricate] — a SELF enters-the-battlefield
 * trigger holding CR 702.123a's consent gate: "you may put N +1/+1 counters on it. If you don't,
 * create N 1/1 colorless Servo artifact creature tokens."
 *
 * The subjects are ad-hoc [card] definitions rather than a printed Kaladesh card, so the test pins
 * the *mechanic* and stays independent of which fabricate cards the corpus happens to hold.
 *
 * Five things worth pinning beyond the happy path:
 *  - the creature must actually **enter** for anything to happen (every case casts it);
 *  - the choice is made **as the trigger resolves**, not as it is put on the stack — the fabricate
 *    play pattern is deciding after the opponent has responded, so `answerFabricate` asserts
 *    nothing is asked before the resolution;
 *  - N comes from the printed keyword, so `Fabricate 3` scales both halves;
 *  - the decline half is a **colorless artifact** creature token, which the counters half must not
 *    create;
 *  - CR 702.123b — two printed instances trigger **separately**, with their own N each, rather than
 *    folding into a single fabricate of the summed N.
 */
class FabricateScenarioTest : FunSpec({

    /** "Fabricate 1" on a plain 2/2 body. */
    val fabricateOne = card("Fabricator Adept") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Human Artificer"
        power = 2
        toughness = 2
        oracleText = "Fabricate 1"
        keywordAbility(KeywordAbility.fabricate(1))
    }

    /** "Fabricate 3" — the N > 1 subject. */
    val fabricateThree = card("Fabricator Adept Prime") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Human Artificer"
        power = 2
        toughness = 2
        oracleText = "Fabricate 3"
        keywordAbility(KeywordAbility.fabricate(3))
    }

    /** Two printed instances — CR 702.123b says each triggers separately. */
    val fabricateTwice = card("Fabricator Adept Twin") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Human Artificer"
        power = 2
        toughness = 2
        oracleText = "Fabricate 1\nFabricate 2"
        keywordAbilities(KeywordAbility.fabricate(1), KeywordAbility.fabricate(2))
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(fabricateOne, fabricateThree, fabricateTwice))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun plusOneCounters(driver: GameTestDriver, perm: EntityId): Int =
        driver.state.getEntity(perm)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    /** Every Servo token on [player]'s battlefield. */
    fun servos(driver: GameTestDriver, player: EntityId): List<EntityId> =
        driver.getPermanents(player).filter { id ->
            driver.state.getEntity(id)?.get<CardComponent>()
                ?.typeLine?.subtypes?.any { it.value == "Servo" } == true
        }

    /** Cast [cardName] from hand with mana granted, and resolve it onto the battlefield. */
    fun castCreature(driver: GameTestDriver, player: EntityId, cardName: String) {
        driver.giveMana(player, Color.GREEN, 3)
        val cardId = driver.putCardInHand(player, cardName)
        val result = driver.submit(CastSpell(player, cardId))
        if (!result.isSuccess) throw AssertionError("cast of $cardName failed: ${result.error}")
        driver.bothPass()
    }

    /**
     * Resolve the fabricate trigger sitting on the stack and answer its consent gate:
     * `takeCounters = true` puts the +1/+1 counters on, `false` declines into the Servos.
     *
     * The gate is answered **as the trigger resolves** — nothing is asked when the ability is put
     * on the stack, which is the timing CR 702.123a's "you may … if you don't …" wording buys.
     * Returns the prompt so a caller can check it names both outcomes.
     */
    fun answerFabricate(driver: GameTestDriver, player: EntityId, takeCounters: Boolean): String {
        withClue("Fabricate must not ask anything before the trigger resolves") {
            driver.pendingDecision shouldBe null
        }
        driver.bothPass()
        val decision = withClue("Fabricate should pause for its yes/no as the trigger resolves") {
            driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        }
        decision.playerId shouldBe player
        val result = driver.submitYesNo(player, takeCounters)
        withClue("Answering the fabricate gate should succeed") { result.error shouldBe null }
        return decision.prompt
    }

    test("fabricate 1 — taking the counters puts a +1/+1 counter on the permanent and makes no Servo") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        castCreature(driver, player, "Fabricator Adept")
        val adept = driver.findPermanent(player, "Fabricator Adept")!!

        val prompt = answerFabricate(driver, player, takeCounters = true)

        withClue("The prompt must name the decline branch too, not just the counters: $prompt") {
            prompt.contains("Servo") shouldBe true
        }
        plusOneCounters(driver, adept) shouldBe 1
        withClue("Taking the counters must not also create Servos") {
            servos(driver, player).size shouldBe 0
        }
    }

    test("fabricate 1 — declining creates one 1/1 colorless Servo artifact creature token") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        castCreature(driver, player, "Fabricator Adept")
        val adept = driver.findPermanent(player, "Fabricator Adept")!!

        answerFabricate(driver, player, takeCounters = false)

        val created = servos(driver, player)
        created.size shouldBe 1
        val token = driver.state.getEntity(created.single())!!.get<CardComponent>()!!
        withClue("The Servo is a 1/1 colorless artifact creature") {
            token.baseStats?.basePower shouldBe 1
            token.baseStats?.baseToughness shouldBe 1
            token.colors shouldBe emptySet<Color>()
            token.typeLine.isArtifact shouldBe true
            token.typeLine.isCreature shouldBe true
        }
        withClue("Declining the counters must not also add them") {
            plusOneCounters(driver, adept) shouldBe 0
        }
    }

    test("fabricate 3 — N comes from the printed keyword and scales the counters branch") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        castCreature(driver, player, "Fabricator Adept Prime")
        val adept = driver.findPermanent(player, "Fabricator Adept Prime")!!

        answerFabricate(driver, player, takeCounters = true)

        plusOneCounters(driver, adept) shouldBe 3
        servos(driver, player).size shouldBe 0
    }

    test("fabricate 3 — N scales the Servo branch too") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        castCreature(driver, player, "Fabricator Adept Prime")
        val adept = driver.findPermanent(player, "Fabricator Adept Prime")!!

        answerFabricate(driver, player, takeCounters = false)

        servos(driver, player).size shouldBe 3
        plusOneCounters(driver, adept) shouldBe 0
    }

    test("CR 702.123b — two printed instances trigger separately, each with its own N") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        castCreature(driver, player, "Fabricator Adept Twin")
        val adept = driver.findPermanent(player, "Fabricator Adept Twin")!!

        // Two independent triggers: take counters for one and Servos for the other. Whichever
        // resolves first, the totals are the same — one instance is worth 1, the other 2.
        answerFabricate(driver, player, takeCounters = true)
        answerFabricate(driver, player, takeCounters = false)

        val counters = plusOneCounters(driver, adept)
        val servoCount = servos(driver, player).size
        withClue("fabricate 1 + fabricate 2 are two choices, not one fabricate 3 " +
            "(got $counters counters and $servoCount Servos)") {
            listOf(1 to 2, 2 to 1) shouldContain (counters to servoCount)
        }
    }
})
