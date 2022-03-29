/**
 * Board.java
 *
 * Create Board class with default and complete
 * constructors. Generates a 20x20 board with
 * 100 Ants and 5 Doodlebugs by default.
 * Contains methods to initialize the board,
 * check if a cell is occupied, place a bug in
 * a cell, and print out the board, as well
 * as many others.
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
import kotlin.jvm.JvmStatic
import project2.Organism
import java.util.*

class Board {
    var randomNumGen = Random()

    val state = State().apply {
        Companion.state = this
    }

    /**
     * Default Constructor.
     *
     * Sets numberOfAnts to 5 and numberOfDoodlebugs to 100.
     * Initialize the board with the given number of
     * ants and doodlebugs.
     */
    constructor() {
        val numberOfAnts = 100
        val numberOfDoodlebugs = 5
        initializeBoard(numberOfAnts, numberOfDoodlebugs)
    }

    /**Complete Constructor
     *
     * Sets numberOfAnts and numberOfDoodlebugs to the
     * given numbers and initializes the board with the given numbers.
     *
     * @param ants Number of ants to initialize the board with
     * @param doodlebugs Number of doodlebugs to initialize the baord with
     */
    constructor(ants: Int, doodlebugs: Int) {
        val numberOfAnts = ants
        val numberOfDoodlebugs = doodlebugs
        initializeBoard(numberOfAnts, numberOfDoodlebugs)
    }

    /**
     * Fills the field randomly with Ants first, then Doodlebugs.
     *
     * @param numAnt The number of ants to fill the board with.
     * @param numDoodlebugs The number of doodlebugs to fill the board with.
     */
    fun initializeBoard(numAnt: Int, numDoodlebugs: Int) {
        //Place Ants in field
        for (i in 0 until numAnt) {
            placeBug(Ant(), numAnt)
        }

        //Place Doodlebugs in field
        for (i in 0 until numDoodlebugs) {
            placeBug(Doodlebug(), numDoodlebugs)
        }
    }

    /**
     * Check if cell is occupied by any type of Organism, return true if occupied, otherwise false
     *
     * @param row Row position to check. Must be 0-19.
     * @param col Column position to check. Must be 0-19.
     * @return True if any type of Organism is occupying the cell. Otherwise false.
     */
    fun isOccupied(row: Int, col: Int): Boolean {
        //Check for null value in given field index
        return if (state.field[row][col] != null) {
            true
        } else {
            false
        }
    }

    /**
     * Place an Organism in an empty cell. Only used for initializing
     * a new board!
     *
     * @param bug The particular type of Organism to be placed. Must be an Ant or Doodlebug.
     * @param numBug The number of bugs to be placed.
     */
    fun placeBug(bug: Organism?, numBug: Int) {
        //Declare and initialize random row and column values
        var row = randomNumGen.nextInt(20)
        var col = randomNumGen.nextInt(20)

        //Cycle through field until an empty cell is found
        while (isOccupied(row, col)) {
            row = randomNumGen.nextInt(20)
            col = randomNumGen.nextInt(20)
        }

        //Place a new Ant in open cell
        if (bug is Ant) {
            state.field[row][col] = Ant(row, col)
        } else if (bug is Doodlebug) {
            state.field[row][col] = Doodlebug(row, col)
        } else {
            println("Error in placeBug method! Did not place a bug in an open cell.")
            System.exit(0)
        }
    }

    /**
     * Return true if there is an Ant in an adjacent cell to Doodlebug
     *
     * @param db Doodlebug that you would like to look for ants near.
     * @return True if there is an Ant adjacent, False if not.
     */
    fun antAdjacent(db: Doodlebug?): Boolean {
        //Top left corner check
        return if (db!!.topFlag && db.leftFlag) {
            if (state.getBug(db.getRowPosition(), db.getColPosition() + 1) is Ant ||
                state.getBug(db.getRowPosition() + 1, db.getColPosition()) is Ant
            ) {
                true
            } else {
                false
            }
        } else if (db.bottomFlag && db.rightFlag) {
            if (state.getBug(db.getRowPosition() - 1, db.getColPosition()) is Ant ||
                state.getBug(db.getRowPosition(), db.getColPosition() - 1) is Ant
            ) {
                true
            } else {
                false
            }
        } else if (db.topFlag && db.rightFlag) {
            if (state.getBug(db.getRowPosition() + 1, db.getColPosition()) is Ant ||
                state.getBug(db.getRowPosition(), db.getColPosition() - 1) is Ant
            ) {
                true
            } else {
                false
            }
        } else if (db.bottomFlag && db.leftFlag) {
            if (state.getBug(db.getRowPosition() - 1, db.getColPosition()) is Ant ||
                state.getBug(db.getRowPosition(), db.getColPosition() + 1) is Ant
            ) {
                true
            } else {
                false
            }
        } else if (db.topFlag) {
            if (state.getBug(db.getRowPosition(), db.getColPosition() + 1) is Ant ||
                state.getBug(db.getRowPosition() + 1, db.getColPosition()) is Ant ||
                state.getBug(db.getRowPosition(), db.getColPosition() - 1) is Ant
            ) {
                true
            } else {
                false
            }
        } else if (db.rightFlag) {
            if (state.getBug(db.getRowPosition() - 1, db.getColPosition()) is Ant ||
                state.getBug(db.getRowPosition() + 1, db.getColPosition()) is Ant ||
                state.getBug(db.getRowPosition(), db.getColPosition() - 1) is Ant
            ) {
                true
            } else {
                false
            }
        } else if (db.bottomFlag) {
            if (state.getBug(db.getRowPosition() - 1, db.getColPosition()) is Ant ||
                state.getBug(db.getRowPosition(), db.getColPosition() + 1) is Ant ||
                state.getBug(db.getRowPosition(), db.getColPosition() - 1) is Ant
            ) {
                true
            } else {
                false
            }
        } else if (db.leftFlag) {
            if (state.getBug(db.getRowPosition() - 1, db.getColPosition()) is Ant ||
                state.getBug(db.getRowPosition(), db.getColPosition() + 1) is Ant ||
                state.getBug(db.getRowPosition() + 1, db.getColPosition()) is Ant
            ) {
                true
            } else {
                false
            }
        } else if (state.getBug(db.getRowPosition() - 1, db.getColPosition()) is Ant ||
            state.getBug(db.getRowPosition(), db.getColPosition() + 1) is Ant ||
            state.getBug(db.getRowPosition() + 1, db.getColPosition()) is Ant ||
            state.getBug(db.getRowPosition(), db.getColPosition() - 1) is Ant
        ) {
            true
        } else {
            false
        }
    }

    /**
     * Return true if there is an empty cell adjacent to the bug
     * @param bug Organism that you would like to check for empty cells nearby.
     * @return True if an empty cell is adjacent. False otherwise.
     */
    fun emptyCellAdjacent(bug: Organism?): Boolean {
        //Top left corner check
        return if (bug!!.topFlag && bug.leftFlag) {
            if (state.getBug(bug.getRowPosition(), bug.getColPosition() + 1) == null ||
                state.getBug(bug.getRowPosition() + 1, bug.getColPosition()) == null
            ) {
                true
            } else {
                false
            }
        } else if (bug.bottomFlag && bug.rightFlag) {
            if (state.getBug(bug.getRowPosition() - 1, bug.getColPosition()) == null ||
                state.getBug(bug.getRowPosition(), bug.getColPosition() - 1) == null
            ) {
                true
            } else {
                false
            }
        } else if (bug.topFlag && bug.rightFlag) {
            if (state.getBug(bug.getRowPosition() + 1, bug.getColPosition()) == null ||
                state.getBug(bug.getRowPosition(), bug.getColPosition() - 1) == null
            ) {
                true
            } else {
                false
            }
        } else if (bug.bottomFlag && bug.leftFlag) {
            if (state.getBug(bug.getRowPosition() - 1, bug.getColPosition()) == null ||
                state.getBug(bug.getRowPosition(), bug.getColPosition() + 1) == null
            ) {
                true
            } else {
                false
            }
        } else if (bug.topFlag) {
            if (state.getBug(bug.getRowPosition(), bug.getColPosition() + 1) == null ||
                state.getBug(bug.getRowPosition() + 1, bug.getColPosition()) == null ||
                state.getBug(bug.getRowPosition(), bug.getColPosition() - 1) == null
            ) {
                true
            } else {
                false
            }
        } else if (bug.rightFlag) {
            if (state.getBug(bug.getRowPosition() - 1, bug.getColPosition()) == null ||
                state.getBug(bug.getRowPosition() + 1, bug.getColPosition()) == null ||
                state.getBug(bug.getRowPosition(), bug.getColPosition() - 1) == null
            ) {
                true
            } else {
                false
            }
        } else if (bug.bottomFlag) {
            if (state.getBug(bug.getRowPosition() - 1, bug.getColPosition()) == null ||
                state.getBug(bug.getRowPosition(), bug.getColPosition() + 1) == null ||
                state.getBug(bug.getRowPosition(), bug.getColPosition() - 1) == null
            ) {
                true
            } else {
                false
            }
        } else if (bug.leftFlag) {
            if (state.getBug(bug.getRowPosition() - 1, bug.getColPosition()) == null ||
                state.getBug(bug.getRowPosition(), bug.getColPosition() + 1) == null ||
                state.getBug(bug.getRowPosition() + 1, bug.getColPosition()) == null
            ) {
                true
            } else {
                false
            }
        } else if (state.getBug(bug.getRowPosition() - 1, bug.getColPosition()) == null ||
            state.getBug(bug.getRowPosition(), bug.getColPosition() + 1) == null ||
            state.getBug(bug.getRowPosition() + 1, bug.getColPosition()) == null ||
            state.getBug(bug.getRowPosition(), bug.getColPosition() - 1) == null
        ) {
            true
        } else {
            false
        }
    }

    /**
     * Print out the current board, along with current number
     * of ants and doodlebugs
     */
    fun printBoard() {
        //Top row border
        println("   01234567890123456789 ")
        for (i in state.field.indices) {
            //Left edge border
            System.out.printf("%2d|", i)
            for (j in 0 until state.field[i].size) {
                //Print whitespace if empty cell
                if (state.field[i][j] == null) {
                    print("-")
                } else {
                    print(state.field[i][j]!!.symbol)
                }
            }
            //Right edge border
            print("|")

            //Prevent printing last empty line
            if (i < state.field.size - 1) {
                println()
            }
        }
        //Bottom row border
        println("\n")
        println("Number of Ants: " + state.numberOfAnts)
        println("Number of Doodlebugs: " + state.numberOfDoodlebugs)
    }

    /**
     * Takes position of bug and removes it, filling with null.
     *
     * @param row Row position of bug you would like to remove.
     * @param col Column position of bug you would like to remove.
     */
    fun removeBug(row: Int, col: Int) {
        if (state.field[row][col] == null) {
            println("Error: removeBug tried to remove a bug, but there was none.")
            System.exit(0)
        }
        if (state.field[row][col] is Ant) {
            state.numberOfAnts--
        }
        if (state.field[row][col] is Doodlebug) {
            state.numberOfDoodlebugs--
        }
        state.field[row][col] = null
    }

    /**
     * Takes a given bug, check if it meets the conditions to breed,
     * and if so, creates a new bug of the same type in an empty
     * adjacent cell.
     *
     * @param bug The Organism, either Ant or Doodlebug, to be checked for breeding
     * eligibility.
     */
    fun breed(bug: Organism?) {
        //Check breed threshold and open adjacent cell
        if (bug!!.timeSinceBreed == bug.breedThreshold && emptyCellAdjacent(bug)) {
            val row = bug.getRowPosition()
            val col = bug.getColPosition()
            val randomDirections = state.randomDirections()
            var breedComplete = false

            //Check for type of bug, ant or doodlebug
            if (bug is Ant) {
                val ant = state.getBug(row, col) as Ant?
                var i = 0
                while (i < 4 && breedComplete == false) {
                    when (randomDirections[i]) {
                        1 -> {
                            if (ant!!.topFlag) break
                            if (state.getBug(row - 1, col) == null) {
                                state.field[row - 1][col] = Ant(row - 1, col)
                                breedComplete = true
                            }
                        }
                        2 -> {
                            if (ant!!.rightFlag) break
                            if (state.getBug(row, col + 1) == null) {
                                state.field[row][col + 1] = Ant(row, col + 1)
                                breedComplete = true
                            }
                        }
                        3 -> {
                            if (ant!!.bottomFlag) break
                            if (state.getBug(row + 1, col) == null) {
                                state.field[row + 1][col] = Ant(row + 1, col)
                                breedComplete = true
                            }
                        }
                        4 -> {
                            if (ant!!.leftFlag) break
                            if (state.getBug(row, col - 1) == null) {
                                state.field[row][col - 1] = Ant(row, col - 1)
                                breedComplete = true
                            }
                        }
                    }
                    i++
                }
                bug.timeSinceBreed = 0 //Reset breeding counter
                state.numberOfAnts++
            } else if (bug is Doodlebug) {
                val db = state.getBug(row, col) as Doodlebug?
                var i = 0
                while (i < 4 && breedComplete == false) {
                    when (randomDirections[i]) {
                        1 -> {
                            if (db!!.topFlag) break
                            if (state.getBug(row - 1, col) == null) {
                                state.field[row - 1][col] = Doodlebug(row - 1, col)
                                breedComplete = true
                            }
                        }
                        2 -> {
                            if (db!!.rightFlag) break
                            if (state.getBug(row, col + 1) == null) {
                                state.field[row][col + 1] = Doodlebug(row, col + 1)
                                breedComplete = true
                            }
                        }
                        3 -> {
                            if (db!!.bottomFlag) break
                            if (state.getBug(row + 1, col) == null) {
                                state.field[row + 1][col] = Doodlebug(row + 1, col)
                                breedComplete = true
                            }
                        }
                        4 -> {
                            if (db!!.leftFlag) break
                            if (state.getBug(row, col - 1) == null) {
                                state.field[row][col - 1] = Doodlebug(row, col - 1)
                                breedComplete = true
                            }
                        }
                    }
                    i++
                }
                bug.timeSinceBreed = 0 //Reset breeding counter
                state.numberOfDoodlebugs++
            } else  //Catch error
            {
                println("Error in breed!")
                System.exit(0)
            }
        }
    }

    /**
     * Check if the given Doodlebug hasn't eaten in a certain number
     * of steps, and if so, it starves, and the Doodlebug is removed.
     *
     * @param db The Doodlebug to be checked for starvation.
     */
    fun starve(db: Doodlebug?) {
        if (db!!.timeSinceEat >= Doodlebug.Companion.STARVE_THRESHOLD) {
            removeBug(db.getRowPosition(), db.getColPosition())
        }
    }

    class State {
        val id = generateId()
        var field = Array(20) { arrayOfNulls<Organism>(20) }
        var numberOfAnts: Int = 0
            get() = this.field.flatten().count { it is Ant }
        var numberOfDoodlebugs: Int = 0
            get() = this.field.flatten().count { it is Doodlebug }

        //Used for scanning efficiently through the field
        var rowScan = 0
        var colScan = 0

        /**
         * Return Organism at given row and column
         * @param row Int value of row position.
         * @param col Int value of column position
         * @return Organism at given location, either Ant or Doodlebug, but must be type casted.
         */
        fun getBug(row: Int, col: Int): Organism? {
            return field[row][col]
        }

        /**
         * Return Organism at position given by array
         * @param position Array with row position in index 0 and column position in index 1.
         * @return Organism at given location, either Ant or Doodlebug, but must be type casted.
         */
        fun getBug(position: IntArray): Organism? {
            return field[position[0]][position[1]]
        }

        /**
         * Returns Integer array of 1 - 4 in a random order.
         * Used for picking a random direction to move, for example.
         *
         * @return Integer array with 4 elements of 1-4 in a random order.
         */
        fun randomDirections(): Array<Int> {
            val directionArray = arrayOf(1, 2, 3, 4)
            Collections.shuffle(Arrays.asList(*directionArray))
            return directionArray
        }

        /**
         * Scans through the field and returns an array of positions in
         * row then column form of all of the Ants in the field.
         *
         * @return Array of 2 element arrays, where the first element is the row,
         * and the second element is the column.
         */
        fun antScanner(): IntArray {
            //System.out.println("DEBUG rowScan colScan: " + rowScan + " " + colScan);
            for (j in colScan..19) {
                //Finish partially completed row
                if (field[rowScan][j] is Ant) {
                    //System.out.println("We have an ant! " + this.rowScan + " " + j); //Debug
                    //Check for last column
                    return if (colScan >= 19) {
                        colScan = 0
                        rowScan++
                        intArrayOf(rowScan - 1, 19)
                    } else {
                        colScan = j + 1
                        intArrayOf(rowScan, j)
                    }
                }
            }
            rowScan++
            colScan = 0
            for (i in rowScan..19) {
                for (j in colScan..19) {
                    //if (field[i][j].getClass() ==  new Ant().getClass()) //Perhaps I should use instanceof?
                    if (field[i][j] is Ant) {
                        return if (j >= 19) {
                            colScan = 0
                            rowScan++
                            intArrayOf(i, 19)
                        } else {
                            colScan = j + 1
                            intArrayOf(i, j)
                        }
                    } else if (j >= 19) {
                        colScan = 0
                        rowScan++
                    }
                }
            }
            rowScan = 0
            colScan = 0
            return intArrayOf(-1, -1)
        }

        /**
         * Scans through the field and returns an array of positions in
         * row then column form of all of the Doodlebugs in the field.
         *
         * @return Array of 2 element arrays, where the first element is the row,
         * and the second element is the column.
         */
        fun doodlebugScanner(): IntArray {
            for (j in colScan..19) {
                //Finish partially completed row
                if (field[rowScan][j] is Doodlebug) {
                    //Check for last column
                    return if (colScan >= 19) {
                        colScan = 0
                        rowScan++
                        intArrayOf(rowScan - 1, 19)
                    } else {
                        colScan = j + 1
                        intArrayOf(rowScan, j)
                    }
                }
            }
            rowScan++
            colScan = 0
            for (i in rowScan..19) {
                for (j in colScan..19) {
                    if (field[i][j] is Doodlebug) {
                        return if (j >= 19) {
                            colScan = 0
                            rowScan++
                            intArrayOf(i, 19)
                        } else {
                            colScan = j + 1
                            intArrayOf(i, j)
                        }
                    }
                }
                colScan = 0
                rowScan++
            }
            rowScan = 0
            colScan = 0
            return intArrayOf(-1, -1)
        }

        /**
         * Takes a bug and a movement direction, and moves the bug to the new cell in the given direction.
         *
         * @param bug Organism, either Ant or Doodlebug, to be moved.
         * @param direction 1, 2, 3, or 4 only. 1 is up, 2 is right, 3 is down, 4 is left.
         */
        fun moveBug(bug: Organism?, direction: Int) {
            var rowPos = bug!!.getRowPosition()
            var colPos = bug.getColPosition()
            if (bug.symbol == 'O') {
                when (direction) {
                    1 -> {
                        field[rowPos - 1][colPos] = Ant(bug as Ant?)
                        getBug(rowPos - 1, colPos)!!.setRowPosition(rowPos - 1)
                        field[rowPos][colPos] = null
                        rowPos--
                    }
                    2 -> {
                        field[rowPos][colPos + 1] = Ant(bug as Ant?)
                        getBug(rowPos, colPos + 1)!!.setColPosition(colPos + 1)
                        field[rowPos][colPos] = null
                        colPos++
                    }
                    3 -> {
                        field[rowPos + 1][colPos] = Ant(bug as Ant?)
                        getBug(rowPos + 1, colPos)!!.setRowPosition(rowPos + 1)
                        field[rowPos][colPos] = null
                        rowPos++
                    }
                    4 -> {
                        field[rowPos][colPos - 1] = Ant(bug as Ant?)
                        getBug(rowPos, colPos - 1)!!.setColPosition(colPos - 1)
                        field[rowPos][colPos] = null
                        colPos--
                    }
                }
            } else if (bug.symbol == 'X') {
                when (direction) {
                    1 -> {
                        field[rowPos - 1][colPos] = Doodlebug(bug as Doodlebug?)
                        getBug(rowPos - 1, colPos)!!.setRowPosition(rowPos - 1)
                        field[rowPos][colPos] = null
                        rowPos--
                    }
                    2 -> {
                        field[rowPos][colPos + 1] = Doodlebug(bug as Doodlebug?)
                        getBug(rowPos, colPos + 1)!!.setColPosition(colPos + 1)
                        field[rowPos][colPos] = null
                        colPos++
                    }
                    3 -> {
                        field[rowPos + 1][colPos] = Doodlebug(bug as Doodlebug?)
                        getBug(rowPos + 1, colPos)!!.setRowPosition(rowPos + 1)
                        field[rowPos][colPos] = null
                        rowPos++
                    }
                    4 -> {
                        field[rowPos][colPos - 1] = Doodlebug(bug as Doodlebug?)
                        getBug(rowPos, colPos - 1)!!.setColPosition(colPos - 1)
                        field[rowPos][colPos] = null
                        colPos--
                    }
                }
                bug.timeSinceBreed++ //Increment counter for breeding
            }
        }
    }

    companion object {
        var state = State()
        var a = 3
    }
}