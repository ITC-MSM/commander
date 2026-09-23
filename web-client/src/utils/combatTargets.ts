import type { ClientCard, EntityId } from '@/types'

/**
 * The player who defends an attack aimed at [targetId] — a player, or a permanent looked up in
 * [cards]. A planeswalker is defended by its controller; a battle by its protector (CR 310.9d),
 * which for a Siege is an opponent of the player who controls it. Reading `controllerId` alone
 * sends every attack on a Siege to the attacker's own seat.
 */
export function defendingPlayerOf(
  targetId: EntityId,
  cards: Readonly<Record<EntityId, ClientCard | undefined>> | null | undefined,
): EntityId {
  const card = cards?.[targetId]
  if (!card) return targetId
  return card.protectorId ?? card.controllerId
}

/** Whether a permanent is a battle (CR 310). */
export function isBattle(card: ClientCard): boolean {
  return card.cardTypes.includes('BATTLE')
}

/**
 * The seat whose side of the table a permanent is drawn on: its controller, except a battle,
 * which sits in front of the player who protects it. That's where attacks on it come from and
 * who blocks for it, so it reads as that player's to defend.
 */
export function tableSideOf(card: ClientCard): EntityId {
  return isBattle(card) && card.protectorId ? card.protectorId : card.controllerId
}
