package com.casino.blackjack.ai;

import com.casino.blackjack.model.Card;
import com.casino.blackjack.model.Hand;

/**
 * Mathematically correct Basic Strategy for Vegas Strip rules (S17, DAS).
 * Powers the hint/probability system.
 */
public class BasicStrategy {

    public enum Action {
        HIT("Hit"), STAND("Stand"),
        DOUBLE("Double (else Hit)"), DOUBLE_STAND("Double (else Stand)"),
        SPLIT("Split"),
        SURRENDER("Surrender (else Hit)"), SURRENDER_STAND("Surrender (else Stand)");
        private final String description;
        Action(String d){this.description=d;}
        public String getDescription(){return description;}
    }

    private BasicStrategy(){}

    public static Action recommend(Hand hand, Card upCard){
        if(hand==null||upCard==null)return Action.HIT;
        int dealer=Math.min(upCard.getValue(),10);
        if(hand.canSplit()){
            Action p=pairSplit(hand,dealer);
            if(p!=null)return p;
        }
        if(hand.isSoft())return softStrategy(hand.getValue(),dealer);
        return hardStrategy(hand.getValue(),dealer);
    }

    private static Action pairSplit(Hand hand, int dealer){
        int pv=hand.getCard(0).getValue();
        if(hand.getCard(0).isAce())return Action.SPLIT;
        return switch(pv){
            case 2,3->(dealer>=4&&dealer<=7)?Action.SPLIT:null;
            case 4->null;
            case 5->null;
            case 6->(dealer>=3&&dealer<=6)?Action.SPLIT:null;
            case 7->(dealer>=2&&dealer<=7)?Action.SPLIT:null;
            case 8->Action.SPLIT;
            case 9->(dealer==7||dealer==10||dealer==11)?null:Action.SPLIT;
            case 10->null;
            default->null;
        };
    }

    private static Action softStrategy(int total, int dealer){
        return switch(total){
            case 13,14->(dealer==5||dealer==6)?Action.DOUBLE:Action.HIT;
            case 15,16->(dealer>=4&&dealer<=6)?Action.DOUBLE:Action.HIT;
            case 17->(dealer>=3&&dealer<=6)?Action.DOUBLE:Action.HIT;
            case 18->{
                if(dealer>=3&&dealer<=6)yield Action.DOUBLE_STAND;
                if(dealer==2||dealer==7||dealer==8)yield Action.STAND;
                yield Action.HIT;
            }
            case 19,20,21->Action.STAND;
            default->Action.HIT;
        };
    }

    private static Action hardStrategy(int total, int dealer){
        if(total>=17)return Action.STAND;
        if(total<=8)return Action.HIT;
        return switch(total){
            case 9->(dealer>=3&&dealer<=6)?Action.DOUBLE:Action.HIT;
            case 10->(dealer>=2&&dealer<=9)?Action.DOUBLE:Action.HIT;
            case 11->dealer<=10?Action.DOUBLE:Action.HIT;
            case 12->(dealer>=4&&dealer<=6)?Action.STAND:Action.HIT;
            case 13,14->(dealer>=2&&dealer<=6)?Action.STAND:Action.HIT;
            case 15->{
                if(dealer==10)yield Action.SURRENDER;
                if(dealer>=2&&dealer<=6)yield Action.STAND;
                yield Action.HIT;
            }
            case 16->{
                if(dealer==10||dealer==11)yield Action.SURRENDER;
                if(dealer>=2&&dealer<=6)yield Action.STAND;
                yield Action.HIT;
            }
            default->Action.HIT;
        };
    }

    public static String explain(Hand hand, Card upCard){
        Action a=recommend(hand,upCard);
        int pv=hand.getValue();
        int dv=upCard!=null?Math.min(upCard.getValue(),10):0;
        String soft=hand.isSoft()?"soft ":"";
        return switch(a){
            case STAND          ->String.format("Strategy: Stand on %s%d vs dealer %d.",soft,pv,dv);
            case HIT            ->String.format("Strategy: Hit on %s%d vs dealer %d.",soft,pv,dv);
            case DOUBLE         ->String.format("Strategy: Double Down on %s%d vs dealer %d.",soft,pv,dv);
            case DOUBLE_STAND   ->String.format("Strategy: Double (else Stand) on %s%d vs dealer %d.",soft,pv,dv);
            case SPLIT          ->"Strategy: Split this pair.";
            case SURRENDER      ->String.format("Strategy: Surrender %s%d vs dealer %d.",soft,pv,dv);
            case SURRENDER_STAND->String.format("Strategy: Surrender (else Stand) on %s%d vs dealer %d.",soft,pv,dv);
        };
    }

    public static double estimateEdge(Hand hand, Card upCard){
        if(hand==null||upCard==null)return 0;
        int dealer=Math.min(upCard.getValue(),10);
        int player=hand.getValue();
        if(player==21)return+0.50;
        if(player>=19)return+0.20;
        if(player==18)return dealer<=8?+0.10:-0.10;
        if(player<=11)return-0.05;
        if(dealer>=7)return-0.15;
        return-0.02;
    }
}
