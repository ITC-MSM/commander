import { test, expect } from '../../fixtures/scenarioFixture'
import { PLAYER_BATTLEFIELD } from '../../helpers/selectors'

/**
 * Tapping a permanent turns it 90deg through a CSS `transform` transition, which only runs if the
 * card's DOM node survives the tap. GameCard wraps every battlefield card in a sizing container
 * for exactly that reason: when the wrapper appeared only for sideways cards, React reconciled the
 * untapped card's own <div> into the new wrapper and mounted a fresh node underneath — and a fresh
 * node paints already rotated, so the turn snapped. This guards the node identity, not the pixels.
 */
test.describe('Tap animation', () => {
  test('a permanent keeps its DOM node when it taps, so the turn can transition', async ({ createGame }) => {
    const { player1 } = await createGame({
      player1Name: 'Tapper',
      player2Name: 'Defender',
      player1: {
        battlefield: [{ name: 'Grizzly Bears', tapped: false, summoningSickness: false }],
      },
      player2: { lifeTotal: 20 },
      phase: 'PRECOMBAT_MAIN',
      activePlayer: 1,
    })

    const p1 = player1.gamePage
    const bears = player1.page.locator(`${PLAYER_BATTLEFIELD} [data-card-id]`).first()
    await expect(bears).not.toHaveAttribute('data-tapped', 'true')

    // The transform leg has to be transitioned for the turn to animate at all.
    const transition = await bears.evaluate((el) => getComputedStyle(el).transitionProperty)
    expect(transition).toContain('transform')

    // Stamp the live node so we can tell afterwards whether React kept it or replaced it.
    const handle = await bears.elementHandle()
    await handle!.evaluate((el: HTMLElement) => { el.dataset.tapAnimationProbe = 'original' })

    await p1.pass()
    await p1.attackAll()

    await expect(bears).toHaveAttribute('data-tapped', 'true')
    // Same node, now rotated: the browser had a from-state to ease out of.
    await expect(bears).toHaveAttribute('data-tap-animation-probe', 'original')
    expect(await bears.evaluate((el) => getComputedStyle(el).transform)).not.toBe('none')
  })
})
