import { test, expect } from '../../fixtures/scenarioFixture'
import { PLAYER_BATTLEFIELD, cardByName } from '../../helpers/selectors'

test('Twinning Glass offers both matching spell faces and casts the chosen creature on the opponent turn', async ({ createGame }) => {
  test.setTimeout(90_000)
  const { player1, player2 } = await createGame({
    player1: {
      hand: ['Mosswood Dreadknight', 'Island'],
      battlefield: [{ name: 'Twinning Glass' }, { name: 'Mountain' }],
      library: ['Island', 'Island', 'Island'],
    },
    player2: {
      hand: ['Mosswood Dreadknight', 'Mosswood Dreadknight'],
      battlefield: ['Forest', 'Swamp', 'Swamp', 'Swamp'].map(name => ({ name })),
      library: ['Island', 'Island', 'Island'],
    },
    phase: 'PRECOMBAT_MAIN',
    activePlayer: 2,
    priorityPlayer: 2,
  })
  const p1 = player1.gamePage
  const p2 = player2.gamePage

  await p2.selectCardInHand('Mosswood Dreadknight')
  await p2.selectAction('Cast Mosswood Dreadknight')
  await expect(player1.page.getByText('Mosswood Dreadknight', { exact: true }).first()).toBeVisible()
  await p1.pass()
  await p2.expectOnBattlefield('Mosswood Dreadknight')
  await p2.selectCardInHand('Mosswood Dreadknight')
  await p2.selectAction('Cast Dread Whispers')
  await expect(player1.page.getByText('Dread Whispers', { exact: true }).first()).toBeVisible()
  await p1.pass()
  await p2.expectLifeTotal(player2.playerId, 19)
  await p2.pass()

  await p1.clickCard('Twinning Glass')
  await p1.selectAction('Cast a spell with a name already cast this turn for free')
  await p2.resolveStack('Twinning Glass ability')
  await p1.selectCardInDecision('Mosswood Dreadknight')
  await p1.confirmSelection()
  await expect(player1.page.getByRole('button', { name: /^Mosswood Dreadknight \(1\)$/ })).toBeVisible()
  await expect(player1.page.getByRole('button', { name: /^Dread Whispers \(1\)$/ })).toBeVisible()
  await p1.screenshot('Both previously cast spell names are offered')
  await p1.selectOption('Mosswood Dreadknight')
  await expect(player1.page.locator(PLAYER_BATTLEFIELD).locator(cardByName('Mosswood Dreadknight'))).toBeVisible()
  await p1.expectHandSize(1)
  await p1.expectTapped('Twinning Glass')
  await p1.screenshot('Creature cast for free during the opponent turn')
})
