package lang;

import lang.modules.*;
import lang.packages.report.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

class ReportInstanceTest {

    @Test
    public void test1() {
        ReportInstance report = new ReportInstance(new Class("report", null));
        report = report.init("name", "this is a test report", "file_2.pdf", true);
        report.addSection("title", "description", "content", "type=text");
        report.addSection("title 2", "this is a second section.", "this is the content", "type=text");
        String json = "{    'LAUNCHPOINTVARSMboSet': {        'rsStart': 0,        'rsCount': 2,        'LAUNCHPOINTVARS': [            {                'rowstamp': '11586934',                'Attributes': {                    'LAUNCHPOINTNAME': {                        'content': 'FLP_TESTPR'                    },                    'AUTOSCRIPT': {                        'content': 'FLP_TESTPR'                    },                    'VARNAME': {                        'content': 'vCapEx'                    },                    'VARBINDINGVALUE': {                        'content': 'INTERNAL'                    },                    'OVERRIDDEN': {                        'content': true                    },                    'LAUNCHPOINTVARSID': {                        'content': 1,                        'resourceid': true                    },                    'VARBINDINGTYPE': {                        'content': 'ATTRIBUTE'                    },                    'LPATTRIBUTEVALUENP': {                        'content': 'INTERNAL'                    }                }            },            {                'rowstamp': '11586935',                'Attributes': {                    'LAUNCHPOINTNAME': {                        'content': 'FLP_TESTPR'                    },                    'AUTOSCRIPT': {                        'content': 'FLP_TESTPR'                    },                    'VARNAME': {                        'content': 'vWO'                    },                    'VARBINDINGVALUE': {                        'content': 'PRLINE.WONUM'                    },                    'OVERRIDDEN': {                        'content': true                    },                    'LAUNCHPOINTVARSID': {                        'content': 2,                        'resourceid': true                    },                    'VARBINDINGTYPE': {                        'content': 'ATTRIBUTE'                    },                    'LPATTRIBUTEVALUENP': {                        'content': 'PRLINE.WONUM'                    }                }            }        ]    }}";
        report.addSection(
                "Launch Point JSON from API",
                "This call returns the JSON from the Maximo API. With this, we get all the information we need and some!",
                json,
                "type=json");
        report.build();
    }

    @Test
    public void test2() {
        assertEquals(1, 1);
    }


    @Test
    public void test3() {
        assertEquals(1, 1);
    }

}
