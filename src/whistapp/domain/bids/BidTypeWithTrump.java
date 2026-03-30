package whistapp.domain.bids;

import whistapp.domain.cards.Suit;

//omdat de humanplayer een suit kan wijzigen bij een bepaald bot, moeten we zorgen dat de strategy bij het kiezen van een bit geen bidtype returned, maar een tuple met bidtype en eventueel een 
//gewijzigde suit
public record BidTypeWithTrump(BidType bidType, Suit newSuit ) {}
