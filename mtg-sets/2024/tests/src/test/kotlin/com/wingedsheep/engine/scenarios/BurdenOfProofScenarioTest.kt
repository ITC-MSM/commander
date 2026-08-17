package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.BurdenOfProof
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Burden of Proof — "Enchanted creature gets +2/+2 as long as it's a Detective you control.
 * Otherwise, it has base power and toughness 1/1 and can't block Detectives."
 *
 * Three statics share one condition and its negation, so the tests exist to prove the condition
 * really splits them rather than one branch quietly always applying. The three cases are the three
 * ways the condition can resolve:
 *
 *  1. a Detective **you** control — the buff branch, and the debuff must be absent;
 *  2. a creature you control that isn't a Detective — the "otherwise" branch, base 1/1;
 *  3. an opponent's Detective — still the "otherwise" branch. This is the case that separates
 *     `Detective` from `Detective you control`; a filter that dropped `youControl` would hand an
 *     opposing Detective +2/+2 instead of shrinking it, which is the opposite of what the card does.
 *
 * The blocking restriction is checked against a real declaration rather than a projected flag,
 * because a restriction that projects but is never enforced is indistinguishable from nothing.
 */
class BurdenOfProofScenarioTest : FunSpec({

    val testDetective = CardDefinition.creature(
        name = "Test Detective",
        manaCost = ManaCost.parse("{1}{U}"),
        subtypes = setOf(Subtype.DETECTIVE),
        power = 2,
        toughness = 2,
    )

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BurdenOfProof)
        driver.registerCard(testDetective)
        return driver
    }

    /** Cast Burden of Proof from [caster]'s hand onto [victim] and resolve it. */
    fun enchant(driver: GameTestDriver, caster: EntityId, victim: EntityId) {
        val aura = driver.putCardInHand(caster, "Burden of Proof")
        driver.giveMana(caster, Color.BLUE, 2)
        driver.castSpell(caster, aura, listOf(victim))
        driver.bothPass()
    }

    test("a Detective you control gets +2/+2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val detective = driver.putCreatureOnBattlefield(active, "Test Detective")
        enchant(driver, active, detective)

        withClue("2/2 Detective + the buff branch") {
            projector.getProjectedPower(driver.state, detective) shouldBe 4
            projector.getProjectedToughness(driver.state, detective) shouldBe 4
        }
    }

    test("a non-Detective you control is shrunk to a base 1/1") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        enchant(driver, active, bears)

        withClue("base power and toughness are set, not modified — a 2/2 becomes 1/1, not 3/3") {
            projector.getProjectedPower(driver.state, bears) shouldBe 1
            projector.getProjectedToughness(driver.state, bears) shouldBe 1
        }
    }

    test("an opponent's Detective takes the 'otherwise' branch — the condition is 'you control'") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val theirDetective = driver.putCreatureOnBattlefield(opponent, "Test Detective")
        enchant(driver, active, theirDetective)

        withClue("it is a Detective, but not one the Aura's controller controls") {
            projector.getProjectedPower(driver.state, theirDetective) shouldBe 1
            projector.getProjectedToughness(driver.state, theirDetective) shouldBe 1
        }
    }

    test("the enchanted non-Detective can't block a Detective, but can block anything else") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val attackingDetective = driver.putCreatureOnBattlefield(active, "Test Detective")
        driver.removeSummoningSickness(attackingDetective)
        val attackingBears = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        driver.removeSummoningSickness(attackingBears)

        val blocker = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        enchant(driver, active, blocker)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(active, listOf(attackingDetective, attackingBears), opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        withClue("the restriction names Detectives specifically") {
            driver.declareBlockers(opponent, mapOf(blocker to listOf(attackingDetective))).error shouldNotBe null
        }
        withClue("a non-Detective attacker is still blockable — it isn't a blanket can't-block") {
            driver.declareBlockers(opponent, mapOf(blocker to listOf(attackingBears))).error shouldBe null
        }
    }
})
