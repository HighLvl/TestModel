/**
 * Doodlebug.java
 *
 * Create the Doodlebug subclass and default constructor
 *
 * Project 2 due May 28, 2014
 *
 * @author Stefano Prezioso
 * @date May 15, 2014
 */
package project2

import project2.Doodlebug
import project2.Board
import project2.Ant
import project2.Simulator
import java.util.Arrays
import kotlin.jvm.JvmStatic
import project2.Organism
import java.util.Collections

class Doodlebug : Organism {
    var timeSinceEat: Int

    /**
     * Empty Constructor
     */
    constructor() : super() {
        symbol = 'X'
        breedThreshold = 8
        timeSinceEat = 0
    }

    /**
     * Complete Constructor.
     * @param row Row position of new Doodlebug
     * @param col Column position of new Doodlebug
     */
    constructor(row: Int, col: Int) : super(row, col) {
        symbol = 'X'
        breedThreshold = 8
        timeSinceEat = 0
    }

    /**
     * Copy Constructor
     * @param d Doodlebug to be copied
     */
    constructor(d: Doodlebug?) : super(d) {
        symbol = d!!.symbol
        breedThreshold = d.breedThreshold
        timeSinceEat = d.timeSinceEat
    }

    /**
     * Takes int 1, 2, 3, or 4, and moves bug up, right, down, or left respectively.
     * @param direction The direction in which to move the bug.
     */
    fun moveBug(direction: Int) {
        if (direction < 1 || direction > 4) {
            println("Error: Improper moveBug parameter.")
            System.exit(0)
        }
        when (direction) {
            1 -> {
                Board.Companion.state.field.get(getRowPosition() - 1)[getColPosition()] = null //Remove Ant
                Board.Companion.state.field.get(getRowPosition() - 1)[getColPosition()] = this //Copy over DB
                Board.Companion.state.field.get(getRowPosition())[getColPosition()] = null //Remove old DB
                setRowPosition(getRowPosition() - 1)
            }
            2 -> {
                Board.Companion.state.field.get(getRowPosition())[getColPosition() + 1] = null //Remove Ant
                Board.Companion.state.field.get(getRowPosition())[getColPosition() + 1] = this //Copy over DB
                Board.Companion.state.field.get(getRowPosition())[getColPosition()] = null //Remove old DB
                setColPosition(getColPosition() + 1)
            }
            3 -> {
                Board.Companion.state.field.get(getRowPosition() + 1)[getColPosition()] = null //Remove Ant
                Board.Companion.state.field.get(getRowPosition() + 1)[getColPosition()] = this //Copy over DB
                Board.Companion.state.field.get(getRowPosition())[getColPosition()] = null //Remove old DB
                setRowPosition(getRowPosition() + 1)
            }
            4 -> {
                Board.Companion.state.field.get(getRowPosition())[getColPosition() - 1] = null //Remove Ant
                Board.Companion.state.field.get(getRowPosition())[getColPosition() - 1] = this //Copy over DB
                Board.Companion.state.field.get(getRowPosition())[getColPosition()] = null //Remove old DB
                setColPosition(getColPosition() - 1)
            }
        }
    }

    /**
     * Returns the position, four flag values, time since last ate,
     * and time since last bred as a String.
     * @return String of information and values about Doodlebug
     */
    override fun toString(): String {
        return """
             Doodlebug at ${getRowPosition()}, ${getColPosition()}
             topFlag ${topFlag}
             rightFlag ${rightFlag}
             bottomFlag ${bottomFlag}
             leftFlag ${leftFlag}
             timeSinceEat: ${timeSinceEat}
             timeSinceBreed: ${timeSinceBreed}
             """.trimIndent()
    }

    companion object {
        const val STARVE_THRESHOLD = 3
    }
}