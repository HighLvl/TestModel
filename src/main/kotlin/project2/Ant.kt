/**
 * Ant.java
 *
 * Create Ant subclass and default constructor
 *
 * Project 2 due May 28, 2014
 *
 * @author Stefano Prezioso
 * @date May 15, 2014
 */
package project2

class Ant : Organism {
    /**
     * Empty Constructor.
     * Sets symbol to 'O' and breedThreshold to 3.
     */
    constructor() : super() {
        symbol = 'O'
        breedThreshold = 3
    }

    /**
     * Complete Constructor.
     * Places Ant at given row and col values, sets symbol to 'O'
     * and breedThreshold to 3.
     * @param row The row position the new Ant will occupy.
     * @param col The column position the new Ant will occupy.
     */
    constructor(row: Int, col: Int) : super(row, col) {
        symbol = 'O'
        breedThreshold = 3
    }

    /**
     * Copy Constructor
     * @param a The Ant object to be copied.
     */
    constructor(a: Ant?) : super(a) {}

    /**
     * Moves called bug in the given direction.
     * @param direction The direction for the bug to be moved. Must be 1, 2, 3, or 4.
     */
    fun moveBug(direction: Int) {
        if (direction < 1 || direction > 4) {
            System.err.println("Error: Improper moveBug parameter.")
            System.exit(0)
        }
        when (direction) {
            1 -> {
                Board.Companion.state.field.get(getRowPosition() - 1)[getColPosition()] = this //Copy over Ant
                Board.Companion.state.field.get(getRowPosition())[getColPosition()] = null //Remove old Ant
                setRowPosition(getRowPosition() - 1)
            }
            2 -> {
                Board.Companion.state.field.get(getRowPosition())[getColPosition() + 1] = this //Copy over Ant
                Board.Companion.state.field.get(getRowPosition())[getColPosition()] = null //Remove old Ant
                setColPosition(getColPosition() + 1)
            }
            3 -> {
                Board.Companion.state.field.get(getRowPosition() + 1)[getColPosition()] = this //Copy over Ant
                Board.Companion.state.field.get(getRowPosition())[getColPosition()] = null //Remove old Ant
                setRowPosition(getRowPosition() + 1)
            }
            4 -> {
                Board.Companion.state.field.get(getRowPosition())[getColPosition() - 1] = this //Copy over Ant
                Board.Companion.state.field.get(getRowPosition())[getColPosition()] = null //Remove old Ant
                setColPosition(getColPosition() - 1)
            }
        }
    }
}