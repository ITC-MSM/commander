import { test, expect } from '../../fixtures/scenarioFixture'
import { cardByName, OPPONENT_BATTLEFIELD } from '../../helpers/selectors'

for (const recipient of ['creature', 'player'] as const) {
  test(`Needle Drop targets the damaged ${recipient} and draws a card`, async ({ createGame }) => {
    test.setTimeout(60_000)
    const { player1, player2 } = await createGame({
      player1: {
        hand: ['Shock', 'Needle Drop'],
        battlefield: [{ name: 'Mountain' }, { name: 'Mountain' }],
        library: ['Forest', 'Forest', 'Forest'],
      },
      player2: {
        battlefield: [{ name: 'Hill Giant' }, { name: 'Grizzly Bears' }],
        library: ['Forest', 'Forest', 'Forest'],
      },
      phase: 'PRECOMBAT_MAIN',
      activePlayer: 1,
    })
    const p1 = player1.gamePage
    await p1.selectCardInHand('Shock')
    await p1.selectAction('Cast')
    if (recipient === 'creature') await p1.selectTarget('Hill Giant')
    else await p1.selectPlayer(player2.playerId)
    await p1.confirmTargets()
    await player2.gamePage.resolveStack('Shock')
    await p1.expectNotInHand('Shock')
    if (recipient === 'player') await p1.expectLifeTotal(player2.playerId, 18)

    await p1.selectCardInHand('Needle Drop')
    await p1.selectAction('Cast')
    if (recipient === 'creature') await p1.selectTarget('Hill Giant')
    else await p1.selectPlayer(player2.playerId)
    await p1.confirmTargets()
    await player2.gamePage.resolveStack('Needle Drop')
    await p1.expectInHand('Forest')
    if (recipient === 'creature') {
      await p1.expectNotOnBattlefield('Hill Giant')
      await expect(player2.page.locator(cardByName('Grizzly Bears')).first()).toBeVisible()
    } else {
      await p1.expectLifeTotal(player2.playerId, 17)
      await player2.gamePage.expectLifeTotal(player2.playerId, 17)
    }
    await expect(player1.page.locator(OPPONENT_BATTLEFIELD).locator(cardByName('Grizzly Bears'))).toBeVisible()
  })
}
