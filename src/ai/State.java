/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;
import java.util.ArrayList;
import kalaha.GameState;

/**
 *
 * @author abdullahamjad
 */

/** 
 * 
 * State class represents every game state with all the required properties
 * The implementation is based on only depth first search with specified depth
 * Hence, the target grade is 'E'
 */

public class State {
    private final GameState mState;                 //individual game state
    private final boolean mTurn;                    //true when its maxs' turn, otherwise false
    private final ArrayList<State> mChildren;       //array to store all child states
    private final int mDepth;                       //variable to store current depth
    private int mUtility;                           //utility of the current state
    private final Integer mAmbo;                    //ambo number
    private boolean mTerminal;                      //true when search reachers terminal state, otherwise false
    
    /**
     * The constructor gets game state, depth, which player's turn is it and the ambo where last played
     * @param gs
     * @param depth
     * @param turn
     * @param ambo
     */
    public State(GameState gs, int depth, boolean turn, Integer ambo) {
        mChildren = new ArrayList();
        mState = gs;
        mTerminal = false;
        mDepth = depth;
        mUtility = 0;
        mTurn = turn;
        mAmbo = ambo;
    }
    /**
     * depth first search upto specified depth
     * @param depth 
     */
    public void depthFirstSearch(int depth) {
        //Continue depth first uptil the specified depth
        if(mDepth < depth) {
            //loop through all possible six child states
            for(int i = 1; i < 6 + 1; i++) {
                GameState gs = mState.clone();
                if(gs.moveIsPossible(i)) {
                    int prevPlayer = gs.getNextPlayer();
                    gs.makeMove(i);
                    State child;
                    if(gs.getNextPlayer() != prevPlayer)   
                        child = new State(gs, (mDepth + 1), !mTurn, i);
                    else
                        child = new State(gs, (mDepth + 1), mTurn, i);
                    mChildren.add(child);
                    //System.out.println(child.mState);
                    child.depthFirstSearch(depth);
                }
            }
            //If a terminal state within the specified depth, get utility
            if(mChildren.isEmpty()) {
                utility();
            }
        }
        //when depth exausts, get the utility
        else {
            utility();
        }
        //Minimax to propagae the utility up the tree
        if(!mTerminal) {
           miniMax();
        }
    }

    private void miniMax() {
        //If it's max's turn get the highest utility value
        if(mTurn) {
            mUtility = mChildren.get(0).mUtility;
            for(int i = 1; i < mChildren.size(); i++) {
                if(mUtility < mChildren.get(i).mUtility)
                    mUtility = mChildren.get(i).mUtility;
            }

        }
        //If it's min's turn get the lowest utility value
        else {
            mUtility = mChildren.get(0).mUtility;
            for(int i = 1; i < mChildren.size(); i++) {
                if(mUtility > mChildren.get(i).mUtility)
                    mUtility = mChildren.get(i).mUtility;
            }
        }
    }
    /**
     * The utility is the difference between the scores
     * More the difference, more is the utility and vice versa
     */
    private void utility() {   
        mTerminal = true;
        int player1;
        int player2;
        if(mTurn) {
            player1 = mState.getNextPlayer();
            if (player1 == 1) {
                player2 = 2;
            }
            else {
                player2 = 1;
            }
        }
        else {
            player2 = mState.getNextPlayer();
            if (player2 == 1) {
                player1 = 2;
            }
            else {
                player1 = 1;
            }
        }
        //player1 is the AI player
        int p1score = mState.getScore(player1);
        //player2 is the opponent
        int p2score = mState.getScore(player2);
        //utility is the difference of score
        mUtility = p1score - p2score;
        //System.out.println("Utility : " + mUtility);
    }
    /** 
     * @param depth
     * @return best move at specified depth
     */
    public int getMove(int depth) {
        int bestMove;
        depthFirstSearch(depth);
        
        int baseUtil = mChildren.get(0).mUtility;
        int state = 0;
        //Find the highest utility value
        for(int i = 0; i < mChildren.size(); i++) {
            if(baseUtil < mChildren.get(i).mUtility) {
                baseUtil = mChildren.get(i).mUtility;
                state = i;
            }
        }
        return mChildren.get(state).mAmbo;
    }
}