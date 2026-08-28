import styles from './learn.module.css'

/** A real card, from the catalog's image (or Scryfall's), with an optional caption under it. */
export function CardImage({ src, name, caption }: { src: string; name: string; caption?: string }) {
  return (
    <div>
      <img src={src} alt={name} className={styles.cardImg} loading="lazy" draggable={false} />
      {caption !== undefined && <div className={styles.cardName}>{caption}</div>}
    </div>
  )
}
