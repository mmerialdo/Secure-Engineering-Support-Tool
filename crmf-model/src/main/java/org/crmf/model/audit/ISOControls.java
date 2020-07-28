/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="ISOControls.java"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

package org.crmf.model.audit;

import org.crmf.model.general.SESTObject;

import java.util.ArrayList;

public class ISOControls extends SESTObject {
    private ArrayList<ISOControl> controls = new ArrayList<ISOControl>();

    public ArrayList<ISOControl> getControls() {
        return controls;
    }

    public void setControls(ArrayList<ISOControl> controls) {
        this.controls = controls;
    }
}
