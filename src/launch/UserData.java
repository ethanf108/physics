/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package launch;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;

/**
 *
 * @author Ethan
 */
public class UserData {

    private String Name;
    private boolean isFix;

    public UserData(String Name, boolean f) {
        this.Name = Name;
        this.isFix = f;
    }

    public static void Generate(Body b, String name, boolean isFix) {
        b.setUserData(new UserData(name, isFix));
    }

    public boolean isFix() {
        return isFix;
    }

    public void setIsFix(boolean isFix) {
        this.isFix = isFix;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

}
