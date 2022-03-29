/**
 * Organism.java
 *
 * Create the Organism superclass with default constructor and
 * getSymbol method.
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

open class Organism {
    val id = generateId()
    var breedThreshold //At what point the Organism breeds
            = 0
    var timeSinceBreed: Int

    /**
     * @return symbol as char
     */
    var symbol //O for Ants, X for Doodlebugs
            = 0.toChar()
    var rowPosition = 0
    var colPosition = 0

    //Flags for against edges to prevent out of bounds exceptions.
    var topFlag = false
    var rightFlag = false
    var bottomFlag = false
    var leftFlag = false

    /**
     * Empty Constructor
     */
    constructor() {
        setRowPosition(-1)
        setColPosition(-1)
        timeSinceBreed = 0
        topFlag = true
        bottomFlag = true
        leftFlag = true
        rightFlag = true
    }

    /**
     * Complete Constructor
     * @param row The row position of the new Organism
     * @param col The column position of the new Organism
     */
    constructor(row: Int, col: Int) {
        setRowPosition(row)
        setColPosition(col)
        timeSinceBreed = 0
    }

    /**
     * Copy Constructor
     * @param o Organism to be copied
     */
    constructor(o: Organism?) {
        breedThreshold = o!!.breedThreshold
        timeSinceBreed = o.timeSinceBreed
        symbol = o.symbol
        setRowPosition(o.getRowPosition())
        setColPosition(o.getColPosition())
    }

    /**
     * @return row position as int
     */
    @JvmName("getRowPosition1")
    fun getRowPosition(): Int {
        return rowPosition
    }

    /**
     * @return column position as int
     */
    @JvmName("getColPosition1")
    fun getColPosition(): Int {
        return colPosition
    }

    /**
     * Sets flags if along edge or in corner of board.
     * @param row New row position
     */
    @JvmName("setRowPosition1")
    fun setRowPosition(row: Int) {
        topFlag = false
        bottomFlag = false
        rowPosition = row
        if (row <= 0) {
            topFlag = true
        } else if (row >= 19) {
            bottomFlag = true
        }
    }

    /**
     * Sets flags if along edge or in corner of board.
     * @param col New column position
     */
    @JvmName("setColPosition1")
    fun setColPosition(col: Int) {
        leftFlag = false
        rightFlag = false
        colPosition = col
        if (col <= 0) {
            leftFlag = true
        } else if (col >= 19) {
            rightFlag = true
        }
    }
}

private var id = 1
fun generateId(): Int = id++