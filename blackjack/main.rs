//use std::io;
//use rand::Rng;
use rand::seq::SliceRandom;

//TO DO
//BETTING (double down)
//SPLITS

struct Card {
    suit:Suit,
    rank:Rank,
    val: u8
}

enum Suit {
    Hearts,
    Diamonds,
    Spades,
    Clubs
}

enum Rank {
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Jack,
    Queen,
    King,
    Ace
}

trait CardStrings {
    fn to_str(&self) -> String;
}

impl CardStrings for Suit {
    fn to_str(&self) -> String {
        match self {
            Self::Hearts => return String::from("Hearts"),
            Self::Diamonds => return String::from("Diamonds"),
            Self::Spades => return String::from("Spades"),
            Self::Clubs => return String::from("Clubs"),
        }
    }
}

impl CardStrings for Rank {
    fn to_str(&self) -> String {
        match self {
            Self::Two => return String::from("Two"),
            Self::Three => return String::from("Three"),
            Self::Four => return String::from("Four"),
            Self::Five => return String::from("Five"),
            Self::Six => return String::from("Six"),
            Self::Seven => return String::from("Seven"),
            Self::Eight => return String::from("Eight"),
            Self::Nine => return String::from("Nine"),
            Self::Ten => return String::from("Ten"),
            Self::Jack => return String::from("Jack"),
            Self::Queen => return String::from("Queen"),
            Self::King => return String::from("King"),
            Self::Ace => return String::from("Ace")
        }
    }
}

impl Card {
    fn to_str(&self) -> String {
        return self.rank.to_str() + " of " + &self.suit.to_str();
    }
    fn blackjack_value(&self, count:u8) -> u8 {
        if matches!(self.rank, Rank::Ace) {
            if count + 11 > 21 {
                return 1;
            } else {
                return 11;
            }
        } else {
            return self.val;
        }
    }
}

fn initalize_cards() -> Vec<Card> {
    let mut deck:Vec<Card> = Vec::new();
    let mut rng = rand::thread_rng();

    for i in 2..15 {
        let heart = create_card(0, i);
        let diamond = create_card(1, i);
        let spade = create_card(2, i);
        let club = create_card(3, i);

        deck.push(heart);
        deck.push(diamond);
        deck.push(spade);
        deck.push(club);
    }
    deck.shuffle(&mut rng);
    return deck;
}

fn create_card(suit_num: u8, rank_num: u8) -> Card {
    let suit = create_suit(suit_num);
    let rank_val = create_rank(rank_num);

    let card = Card {
        suit: suit,
        rank: rank_val.0,
        val:  if rank_val.1 > 10 && rank_val.1 < 14 { 10 } else { rank_val.1}
    };

    return card;
}

fn create_suit(num: u8) -> Suit {
    let suit_val:u8 = num;
    let card_suit:Suit;

    match suit_val {
        0 => card_suit = Suit::Hearts,
        1 => card_suit = Suit::Diamonds,
        2 => card_suit = Suit::Spades,
        _ => card_suit = Suit::Clubs
    }
    return card_suit;
}

fn create_rank(num: u8) -> (Rank, u8) {
    let rank_val:u8 = num;
    let card_rank:Rank;
    match rank_val {
        2 => card_rank = Rank::Two,
        3 => card_rank = Rank::Three,
        4 => card_rank = Rank::Four,
        5 => card_rank = Rank::Five,
        6 => card_rank = Rank::Six,
        7 => card_rank = Rank::Seven,
        8 => card_rank = Rank::Eight,
        9 => card_rank = Rank::Nine,
        10 => card_rank = Rank::Ten,
        11 => card_rank = Rank::Jack,
        12 => card_rank = Rank::Queen,
        13 => card_rank = Rank::King,
        _ => card_rank = Rank::Ace
    }
    return (card_rank, rank_val);
}

fn hit(deck: &mut Vec<Card>, mut count: u8, bet_int: u32, mut chips: u32) -> u8 {
    println!("HIT");
    let new_card = deck.pop().unwrap();

    count = count + new_card.blackjack_value(count);
    println!("Dealt a {}", new_card.to_str());
    println!("Total count: {}", count);
    if count > 21 {
        println!("You lose! {} subtracted from your chips", bet_int);
        chips -= bet_int;
        println!("You have {} chips remaining", chips);
    }
    return count;
}

fn stand(deck: &mut Vec<Card>, mut dealer_count: u8, count: u8, mut chips: u32, bet_int: u32) {
    println!("STAND");

    loop {
        let new_dealer_card = deck.pop().unwrap();
        println!("Dealer draws a {}", new_dealer_card.to_str());
        if dealer_count < 17 {
            dealer_count += new_dealer_card.blackjack_value(dealer_count);
        } else {
            if dealer_count == count {
                println!("Tie game! Dealer had a hand value of {}", dealer_count);
                break;
            } else if dealer_count > count && dealer_count <= 21 {
                println!("You lose! Dealer had a hand value of {}", dealer_count);
                break;
            } else {
                println!("You win! Dealer had a hand value of {}", dealer_count);
                chips += bet_int * 2;
                println!("You won {} chips, current total is {}", bet_int * 2, chips);
                break;
            }
        }
    }
}

fn play_again() -> bool {
    println!("Would you like to play again? (y/n)");
    let mut input = String::new();
    std::io::stdin().read_line(&mut input).unwrap();

    if input.trim() == "y" {
        return true;
    } else {
        return false;
    }
}

fn distribute_cards(deck: &mut Vec<Card>) -> (Card, Card, Card, Card) {
    let card1 = deck.pop().unwrap();
    let dealer_card1 = deck.pop().unwrap();
    let card2 = deck.pop().unwrap();
    let dealer_card2 = deck.pop().unwrap();

    return (card1, dealer_card1, card2, dealer_card2);
}

fn print_cards(dealer_card: Card, card1: Card, card2: Card) {
    println!();
    println!("----Dealers visible card----");
    println!("{}", dealer_card.to_str());
    println!("----Your cards are----");
    println!("{}", card1.to_str());
    println!("{}", card2.to_str());
    println!();
    println!("Total count: {}", card1.val + card2.val);
    println!("Type your bet (number):");
}

fn main() {
    let mut deck:Vec<Card> = initalize_cards();
    //let mut rng = rand::thread_rng();

    let mut count:u8 = 0;
    let mut dealer_count:u8 = 0;
    let distributor = distribute_cards(&mut deck);
    let card1 = distributor.0;
    let dealer_card1 = distributor.1;
    let card2 = distributor.2;
    let dealer_card2 = distributor.3;

    let mut chips:u32 = 20;
    let mut is_playing = true;

    count = count + card1.blackjack_value(count) + card2.blackjack_value(count);
    dealer_count = dealer_count + dealer_card1.blackjack_value(dealer_count)
                              + dealer_card2.blackjack_value(dealer_count);

    println!("Welcome to Quinn's incomplete Blackjack!");
    print_cards(dealer_card1, card1, card2);

    let mut bet_str = String::new();
    std::io::stdin().read_line(&mut bet_str).unwrap();
    let bet_int:u32 = bet_str.trim().parse::<u32>().unwrap();
    chips -= bet_int;

    while is_playing {
        let mut input = String::new();
        println!("Type h to hit, s to stand, and q to quit");
        std::io::stdin().read_line(&mut input).unwrap();

        if input.trim() == "h" {
            count = hit(&mut deck, count, bet_int, chips);
            if count > 21 {
                is_playing = play_again();
            }
        } else if input.trim() == "s" {
            stand(&mut deck, dealer_count, count, chips, bet_int);
            is_playing = play_again();
            if is_playing {
                let distributor = distribute_cards(&mut deck);

                let card1 = distributor.0;
                let dealer_card1 = distributor.1;
                let card2 = distributor.2;
                let dealer_card2 = distributor.3;

                count = card1.blackjack_value(count) + card2.blackjack_value(count);
                dealer_count = dealer_card1.blackjack_value(dealer_count)
                                + dealer_card2.blackjack_value(dealer_count);
        
                println!("----Dealers visible card----");
                println!("{}", dealer_card1.to_str());
                println!("----Your cards are----");
                println!("{}", card1.to_str());
                println!("{}", card2.to_str());
                println!();
                println!("Total count: {}", count);
            }
        } else if input.trim() == "q" {
            is_playing = false;
        } else {
            println!("Type h for hit, s for stand, or q for quit");
        }
    }
}
