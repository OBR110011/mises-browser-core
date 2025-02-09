#ifndef MISES_BROWSER_EXTENSIONS_EXTENSION_TAB_UTIL_H_
#define MISES_BROWSER_EXTENSIONS_EXTENSION_TAB_UTIL_H_

#include "src/chrome/browser/extensions/extension_tab_util.h"

namespace extensions{

#if BUILDFLAG(IS_ANDROID) 
void CreateTabObjectAndroid(   
    api::tabs::Tab* tab_object,
    content::WebContents* contents,
    const Extension* extension,
    int tab_index);
void CreateTabListAndroid(
    const Browser* browser,
    const Extension* extension,
    Feature::Context context,
		base::Value::List& tab_list);
bool GetTabByIdAndroid(int tab_id, content::WebContents** contents, int* tab_index);
base::Value::Dict CreateDummyWindowValueForExtension(
		  ExtensionTabUtil::PopulateTabBehavior populate_tab_behavior);
Browser* FindBrowserForWindowAndroid(Profile* profile, int window_id);
#endif

}  // namespace extensions

#endif  // CHROME_BROWSER_EXTENSIONS_EXTENSION_TAB_UTIL_H_
