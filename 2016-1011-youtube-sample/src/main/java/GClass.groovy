/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
class GClass {
    def name

    def greet() { "Hello ${name}" }
    
    static main(args) {
        def helloWorld = new GClass()
        helloWorld.name = "Groovy"
        println helloWorld.greet()
    }
}

