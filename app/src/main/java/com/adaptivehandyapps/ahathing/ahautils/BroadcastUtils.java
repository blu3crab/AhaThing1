/**
 * Copyright © 2015 Adaptive Handy Apps, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
// Project: AHA Smart Energy Explorer
// Contributor(s): M.A.Tucker
// Origination: SEP 2015
package com.adaptivehandyapps.ahathing.ahautils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by mat on 9/3/2015.
 */
public class BroadcastUtils {

    static final public String AHA_REFRESH = "com.brainpipes.brainpipesy.REQUEST_REFRESH";

    static final public String AHA_MESSAGE = "com.brainpipes.brainpipesy.MESSAGE";

    //////////////////////////////////////////////////////////////////////////////////////////
    // notify listeners of result
    public static void broadcastResult(Context context, String request, String message) {
        // instantiate broadcaster
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(request);
        if(message != null)
            intent.putExtra(AHA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

}
