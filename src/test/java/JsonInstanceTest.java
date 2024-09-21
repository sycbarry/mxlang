package lang;

import lang.modules.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;


class JsonInstanceTest {

    @Test
    public void test1() {
        HttpInstance instance = new HttpInstance(new Class("http", null));
        String json = "{    'LAUNCHPOINTVARSMboSet': {        'rsStart': 0,        'rsCount': 2,        'LAUNCHPOINTVARS': [            {                'rowstamp': '11586934',                'Attributes': {                    'LAUNCHPOINTNAME': {                        'content': 'FLP_TESTPR'                    },                    'AUTOSCRIPT': {                        'content': 'FLP_TESTPR'                    },                    'VARNAME': {                        'content': 'vCapEx'                    },                    'VARBINDINGVALUE': {                        'content': 'INTERNAL'                    },                    'OVERRIDDEN': {                        'content': true                    },                    'LAUNCHPOINTVARSID': {                        'content': 1,                        'resourceid': true                    },                    'VARBINDINGTYPE': {                        'content': 'ATTRIBUTE'                    },                    'LPATTRIBUTEVALUENP': {                        'content': 'INTERNAL'                    }                }            },            {                'rowstamp': '11586935',                'Attributes': {                    'LAUNCHPOINTNAME': {                        'content': 'FLP_TESTPR'                    },                    'AUTOSCRIPT': {                        'content': 'FLP_TESTPR'                    },                    'VARNAME': {                        'content': 'vWO'                    },                    'VARBINDINGVALUE': {                        'content': 'PRLINE.WONUM'                    },                    'OVERRIDDEN': {                        'content': true                    },                    'LAUNCHPOINTVARSID': {                        'content': 2,                        'resourceid': true                    },                    'VARBINDINGTYPE': {                        'content': 'ATTRIBUTE'                    },                    'LPATTRIBUTEVALUENP': {                        'content': 'PRLINE.WONUM'                    }                }            }        ]    }}";
        Map<String, Object> jsonObject = instance.toJsonObject(
                json);
        JsonInstance jsonInstance = new JsonInstance(
                new Class("json", null), jsonObject, json, false);
        String internal = (String) jsonInstance.getFromPath(
            jsonObject,
            "LAUNCHPOINTVARSMboSet.LAUNCHPOINTVARS.array.0.Attributes.LPATTRIBUTEVALUENP.content.value",
            false);
        assertEquals(internal, "INTERNAL");
        // System.out.println(jsonInstance.getParsedJson());
    }

    @Test
    public void test2() {
        HttpInstance instance = new HttpInstance(new Class("http", null));
        String json = "{    'LAUNCHPOINTVARSMboSet': {        'rsStart': 0,        'rsCount': 2,        'LAUNCHPOINTVARS': [            {                'rowstamp': '11586934',                'Attributes': {                    'LAUNCHPOINTNAME': {                        'content': 'FLP_TESTPR'                    },                    'AUTOSCRIPT': {                        'content': 'FLP_TESTPR'                    },                    'VARNAME': {                        'content': 'vCapEx'                    },                    'VARBINDINGVALUE': {                        'content': 'INTERNAL'                    },                    'OVERRIDDEN': {                        'content': true                    },                    'LAUNCHPOINTVARSID': {                        'content': 1,                        'resourceid': true                    },                    'VARBINDINGTYPE': {                        'content': 'ATTRIBUTE'                    },                    'LPATTRIBUTEVALUENP': {                        'content': 'INTERNAL'                    }                }            },            {                'rowstamp': '11586935',                'Attributes': {                    'LAUNCHPOINTNAME': {                        'content': 'FLP_TESTPR'                    },                    'AUTOSCRIPT': {                        'content': 'FLP_TESTPR'                    },                    'VARNAME': {                        'content': 'vWO'                    },                    'VARBINDINGVALUE': {                        'content': 'PRLINE.WONUM'                    },                    'OVERRIDDEN': {                        'content': true                    },                    'LAUNCHPOINTVARSID': {                        'content': 2,                        'resourceid': true                    },                    'VARBINDINGTYPE': {                        'content': 'ATTRIBUTE'                    },                    'LPATTRIBUTEVALUENP': {                        'content': 'PRLINE.WONUM'                    }                }            }        ]    }}";
        Map<String, Object> jsonObject = instance.toJsonObject(
                json);
        JsonInstance jsonInstance = new JsonInstance(
                new Class("json", null), jsonObject, json, false);
        double internal = (double) jsonInstance.getFromPath(
            jsonObject,
            "LAUNCHPOINTVARSMboSet.rsStart.value",
            false);
        assertEquals(internal, 0);
    }


    @Test
    public void test3() {
        HttpInstance instance = new HttpInstance(new Class("http", null));
        String json = "{    'LAUNCHPOINTVARSMboSet': {        'rsStart': 0,        'rsCount': 2,        'LAUNCHPOINTVARS': [            {                'rowstamp': '11586934',                'Attributes': {                    'LAUNCHPOINTNAME': {                        'content': 'FLP_TESTPR'                    },                    'AUTOSCRIPT': {                        'content': 'FLP_TESTPR'                    },                    'VARNAME': {                        'content': 'vCapEx'                    },                    'VARBINDINGVALUE': {                        'content': 'INTERNAL'                    },                    'OVERRIDDEN': {                        'content': true                    },                    'LAUNCHPOINTVARSID': {                        'content': 1,                        'resourceid': true                    },                    'VARBINDINGTYPE': {                        'content': 'ATTRIBUTE'                    },                    'LPATTRIBUTEVALUENP': {                        'content': 'INTERNAL'                    }                }            },            {                'rowstamp': '11586935',                'Attributes': {                    'LAUNCHPOINTNAME': {                        'content': 'FLP_TESTPR'                    },                    'AUTOSCRIPT': {                        'content': 'FLP_TESTPR'                    },                    'VARNAME': {                        'content': 'vWO'                    },                    'VARBINDINGVALUE': {                        'content': 'PRLINE.WONUM'                    },                    'OVERRIDDEN': {                        'content': true                    },                    'LAUNCHPOINTVARSID': {                        'content': 2,                        'resourceid': true                    },                    'VARBINDINGTYPE': {                        'content': 'ATTRIBUTE'                    },                    'LPATTRIBUTEVALUENP': {                        'content': 'PRLINE.WONUM'                    }                }            }        ]    }}";
        Map<String, Object> jsonObject = instance.toJsonObject(
                json);
        JsonInstance jsonInstance = new JsonInstance(
                new Class("json", null), jsonObject, json, false);
        String internal = (String) jsonInstance.getFromPath(
            jsonObject,
            "LAUNCHPOINTVARSMboSet.LAUNCHPOINTVARS.array.0.Attributes.VARBINDINGTYPE.content.value",
            false);
        assertEquals(internal, "ATTRIBUTE");
    }

}
