/**
 * Simulator.java
 *
 * Contains main method to test the other classes of
 * Project 2, as well as methods to establish the
 * sequence of events for the simulation.
 *
 * Project 2 due May 28, 2014
 *
 * @author Stefano Prezioso
 * @date May 15, 2014
 */
package project2

import java.util.*

object Simulator {
    /**
     * Waits for user to press enter to continue the program.
     */
    fun enterToContinue() {
        val keyboard = Scanner(System.`in`)
        println("Press enter to continue: ")
        keyboard.nextLine()
    }

    /**
     * Takes array of coordinates of a Doodlebug and eats a random, adjacent Ant
     * if one is present
     *
     * @param coordinates Array with two elements in order row then column
     */
    fun eatAnt(coordinates: IntArray) {
        //Get Doodlebug from coordinates
        val db = Board.Companion.state.getBug(coordinates[0], coordinates[1]) as Doodlebug
        val randomDirections: Array<Int> = Board.Companion.state.randomDirections() //Random direction list
        var moveComplete = false

        //Check four random directions for Ant, and eat first found
        var i = 0
        while (i < 4 && moveComplete == false) {
            when (randomDirections[i]) {
                1 -> {
                    if (db.topFlag) break
                    if (Board.Companion.state.getBug(coordinates[0] - 1, coordinates[1]) is Ant) {
                        Board.Companion.state.field.get(coordinates[0] - 1)[coordinates[1]] = null
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 1)
                        moveComplete = true
                        Board.Companion.state.numberOfAnts--
                    }
                }
                2 -> {
                    if (db.rightFlag) break
                    if (Board.Companion.state.getBug(coordinates[0], coordinates[1] + 1) is Ant) {
                        Board.Companion.state.field.get(coordinates[0])[coordinates[1] + 1] = null
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 2)
                        moveComplete = true
                        Board.Companion.state.numberOfAnts--
                    }
                }
                3 -> {
                    if (db.bottomFlag) break
                    if (Board.Companion.state.getBug(coordinates[0] + 1, coordinates[1]) is Ant) {
                        Board.Companion.state.field.get(coordinates[0] + 1)[coordinates[1]] = null
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 3)
                        moveComplete = true
                        Board.Companion.state.numberOfAnts--
                    }
                }
                4 -> {
                    if (db.leftFlag) break
                    if (Board.Companion.state.getBug(coordinates[0], coordinates[1] - 1) is Ant) {
                        Board.Companion.state.field.get(coordinates[0])[coordinates[1] - 1] = null
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 4)
                        moveComplete = true
                        Board.Companion.state.numberOfAnts--
                    }
                }
            }
            i++
        }
    }

    /**
     * Moves Doodlebug at given coordinates to random empty adjacent cell.
     *
     * @param coordinates Array with two elements in order row then column
     */
    fun moveDoodlebug(coordinates: IntArray) {
        val row = coordinates[0]
        val col = coordinates[1]
        val db = Board.Companion.state.getBug(row, col) as Doodlebug
        val randomDirections: Array<Int> = Board.Companion.state.randomDirections()
        var moveComplete = false
        var i = 0
        while (i < 4 && moveComplete == false) {
            when (randomDirections[i]) {
                1 -> {
                    if (db.topFlag) break
                    if (Board.Companion.state.getBug(coordinates[0] - 1, coordinates[1]) == null) {
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 1)
                        moveComplete = true
                    }
                }
                2 -> {
                    if (db.rightFlag) break
                    if (Board.Companion.state.getBug(coordinates[0], coordinates[1] + 1) == null) {
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 2)
                        moveComplete = true
                    }
                }
                3 -> {
                    if (db.bottomFlag) break
                    if (Board.Companion.state.getBug(coordinates[0] + 1, coordinates[1]) == null) {
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 3)
                        moveComplete = true
                    }
                }
                4 -> {
                    if (db.leftFlag) break
                    if (Board.Companion.state.getBug(coordinates[0], coordinates[1] - 1) == null) {
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 4)
                        moveComplete = true
                    }
                }
            }
            i++
        }
    }

    /**
     * Moves ant at given coordinates to random empty adjacent cell.
     *
     * @param coordinates Array with two elements in order row then column
     */
    fun moveAnt(coordinates: IntArray) {
        val row = coordinates[0]
        val col = coordinates[1]
        val ant = Board.Companion.state.getBug(row, col) as Ant
        val randomDirections: Array<Int> = Board.Companion.state.randomDirections()
        var moveComplete = false
        var i = 0
        while (i < 4 && moveComplete == false) {
            when (randomDirections[i]) {
                1 -> {
                    if (ant.topFlag) break
                    if (Board.Companion.state.getBug(coordinates[0] - 1, coordinates[1]) == null) {
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 1)
                        moveComplete = true
                    }
                }
                2 -> {
                    if (ant.rightFlag) break
                    if (Board.Companion.state.getBug(coordinates[0], coordinates[1] + 1) == null) {
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 2)
                        moveComplete = true
                    }
                }
                3 -> {
                    if (ant.bottomFlag) break
                    if (Board.Companion.state.getBug(coordinates[0] + 1, coordinates[1]) == null) {
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 3)
                        moveComplete = true
                    }
                }
                4 -> {
                    if (ant.leftFlag) break
                    if (Board.Companion.state.getBug(coordinates[0], coordinates[1] - 1) == null) {
                        Board.Companion.state.moveBug(Board.Companion.state.getBug(coordinates[0], coordinates[1]), 4)
                        moveComplete = true
                    }
                }
            }
            i++
        }
    }

    /**
     * Returns an array of position arrays of all the ants
     * on the field.
     *
     * @return Array with two columns with positions in row then column order.
     * Each row is a new ant position.
     */
    fun antPositions(): Array<IntArray> {
        val positions = Array(Board.Companion.state.numberOfAnts) { IntArray(2) }
        for (i in 0 until Board.Companion.state.numberOfAnts) {
            positions[i] = Board.Companion.state.antScanner()
        }
        Board.Companion.state.rowScan = 0
        Board.Companion.state.colScan = 0
        return positions
    }

    /**
     * Returns an array of position arrays of all the doodlebugs
     * on the field.
     *
     * @return Array with two columns with positions in row then column order.
     * Each row is a new doodlebug position.
     */
    fun doodlebugPositions(): Array<IntArray> {
        val positions = Array(Board.Companion.state.numberOfDoodlebugs) { IntArray(2) }
        for (i in 0 until Board.Companion.state.numberOfDoodlebugs) {
            positions[i] = Board.Companion.state.doodlebugScanner()
        }
        Board.Companion.state.rowScan = 0
        Board.Companion.state.colScan = 0
        return positions
    }

    /**
     * Used for debugging purposes. Prints out all the positions of
     * all of the Doodlebugs and Ants, in that order.
     */
    fun printPositions() {
        println("Doodlebugs")
        var dbPositions = Array(Board.Companion.state.numberOfDoodlebugs) { IntArray(2) }
        dbPositions = doodlebugPositions()
        for (i in 0 until Board.Companion.state.numberOfDoodlebugs) {
            println(Arrays.toString(dbPositions[i]))
        }
        println("Ants")
        var positions = Array(Board.Companion.state.numberOfAnts) { IntArray(2) }
        positions = antPositions()
        for (i in 0 until Board.Companion.state.numberOfAnts) {
            println(Arrays.toString(positions[i]))
        }
    }

    /**
     * Establishes the proper sequence of events for the simulation
     * given the particular rules.
     *
     * 1. Check Doodlebugs for an adjacent Ant, and if so, eat it.
     * 2. If no adjacent Ant, the Doodlebugs move to an empty, open cell
     * if one is present.
     * 3. The Ants move to an empty, open cell, if one is present.
     * 4. Doodlebugs breed, if eligibile.
     * 5. Ants breed, if eligible.
     * 6. Doodlebugs starve to death if they haven't eaten recently enough.
     *
     * @param testBoard
     */
    fun turnSequence(testBoard: Board) {
        var dbPositions = Array(Board.Companion.state.numberOfDoodlebugs) { IntArray(2) }
        dbPositions = doodlebugPositions()
        var position = IntArray(2)

        /* ********* DOODLEBUGS EAT/MOVE *********** */for (i in 0 until Board.Companion.state.numberOfDoodlebugs) {
            //Get Doodlebug Coordinates
            position = dbPositions[i]
            //position = testBoard.doodlebugScanner();			

            //Error checking
            if (position[0] < 0 || position[1] < 0) {
                println("-1 from doodlebugScanner") //debug
                System.exit(0)
            } else if (position[0] > 19 || position[1] > 19) {
                println("20 from doodlebugScanner") //debug
                System.exit(0)
            }

            //Eat Ant if necessary
            if (testBoard.antAdjacent(Board.Companion.state.getBug(position) as Doodlebug)) {
                val db = Board.Companion.state.getBug(position) as Doodlebug
                db.timeSinceEat = 0
                db.timeSinceBreed++
                eatAnt(position)
            } else {
                //Else move doodlebug
                val db = Board.Companion.state.getBug(position) as Doodlebug
                db.timeSinceEat++
                db.timeSinceBreed++
                moveDoodlebug(position)
            }
        }

        /* ********* ANTS MOVE *********** */
        var antPositions = Array(Board.Companion.state.numberOfAnts) { IntArray(2) }
        antPositions = antPositions()

        //Loop through all the ants
        for (i in 0 until Board.Companion.state.numberOfAnts) {
            //Get Ant position
            position = antPositions[i]
            if (position[0] < 0 || position[1] < 0) {
                break
            }

            //Move Ant
            val ant = Board.Companion.state.getBug(position) as Ant
            ant.timeSinceBreed++
            moveAnt(position)
        }

        /* ********* DOODLEBUGS BREED *********** */Board.Companion.state.rowScan = 0
        Board.Companion.state.colScan = 0

        //For all the doodlebugs...
        dbPositions = doodlebugPositions()
        var tempNumberOfDoodlebugs: Int = Board.Companion.state.numberOfDoodlebugs
        for (i in 0 until tempNumberOfDoodlebugs) {
            //Get Doodlebug Coordinates	
            position = dbPositions[i]
            val row = position[0]
            val col = position[1]
            if (row < 0 || col < 0) {
                break
            }
            testBoard.breed(Board.Companion.state.getBug(position))
        }

        /* ********* ANTS BREED *********** */antPositions = antPositions()
        val tempNumberOfAnts: Int = Board.Companion.state.numberOfAnts
        for (i in 0 until tempNumberOfAnts) {
            //Get Ant Coordinates	
            position = antPositions[i]
            val row = position[0]
            val col = position[1]
            if (row < 0 || col < 0) {
                break
            }
            testBoard.breed(Board.Companion.state.getBug(row, col))
        }

        /* ********* DOODLEBUGS STARVE *********** */dbPositions = doodlebugPositions()
        tempNumberOfDoodlebugs = Board.Companion.state.numberOfDoodlebugs
        for (i in 0 until tempNumberOfDoodlebugs) {
            testBoard.starve(Board.Companion.state.getBug(dbPositions[i][0], dbPositions[i][1]) as Doodlebug)
        }
    }

}