#ifndef MISES_COMMON_WEBUI_URL_CONSTANTS_H_
#define MISES_COMMON_WEBUI_URL_CONSTANTS_H_


#include "src/chrome/common/webui_url_constants.h"

namespace chrome {

#if BUILDFLAG(IS_ANDROID)
extern const char kChromeUIAppServiceInternalsHost[];
extern const char kChromeUIAppServiceInternalsURL[];
extern const char kChromeUINearbyInternalsHost[];
extern const char kChromeUINearbyInternalsURL[];
extern const char kChromeUIBookmarksSidePanelHost[];
extern const char kChromeUIBookmarksSidePanelURL[];
extern const char kChromeUICustomizeChromeSidePanelHost[];
extern const char kChromeUICustomizeChromeSidePanelURL[];
extern const char kChromeUIHistoryClustersSidePanelHost[];
extern const char kChromeUIHistoryClustersSidePanelURL[];
extern const char kChromeUIReadLaterHost[];
extern const char kChromeUIReadLaterURL[];
extern const char kChromeUIUntrustedCompanionSidePanelHost[];
extern const char kChromeUIUntrustedCompanionSidePanelURL[];
extern const char kChromeUIUntrustedFeedURL[];
extern const char kChromeUIUntrustedReadAnythingSidePanelHost[];
extern const char kChromeUIUntrustedReadAnythingSidePanelURL[];
extern const char kChromeUIUserNotesSidePanelHost[];
extern const char kChromeUIUserNotesSidePanelURL[];
extern const char kChromeUIWebAppInternalsHost[];
extern const char kChromeUIWebAppInternalsURL[];
extern const char kChromeUIWebUITestHost[];
extern const char kChromeUIUntrustedWebUITestURL[];
#endif  // BUILDFLAG(IS_ANDROID)


#if BUILDFLAG(IS_ANDROID) 
extern const char kChromeUIDiscardsHost[];
extern const char kChromeUIDiscardsURL[];
#endif

#if BUILDFLAG(IS_ANDROID) 
extern const char kChromeUIWebAppSettingsURL[];
extern const char kChromeUIWebAppSettingsHost[];
#endif


#if BUILDFLAG(IS_ANDROID) 
extern const char kChromeUIBrowserSwitchHost[];
extern const char kChromeUIBrowserSwitchURL[];
extern const char kChromeUIEnterpriseProfileWelcomeHost[];
extern const char kChromeUIEnterpriseProfileWelcomeURL[];
extern const char kChromeUIProfileCustomizationHost[];
extern const char kChromeUIProfileCustomizationURL[];
extern const char kChromeUIProfilePickerHost[];
extern const char kChromeUIProfilePickerUrl[];
extern const char kChromeUIProfilePickerStartupQuery[];
#endif


#if BUILDFLAG(IS_ANDROID)
extern const char kChromeUICommanderHost[];
extern const char kChromeUICommanderURL[];
extern const char kChromeUIDownloadShelfHost[];
extern const char kChromeUIDownloadShelfURL[];
extern const char kChromeUITabSearchHost[];
extern const char kChromeUITabSearchURL[];
#endif

#if BUILDFLAG(IS_ANDROID)
extern const char kChromeUIOnDeviceInternalsHost[];
extern const char kChromeUISearchEngineChoiceURL[];
extern const char kChromeUISearchEngineChoiceHost[];
#endif

#if BUILDFLAG(IS_ANDROID)
extern const char kChromeUIUntrustedHatsHost[];
extern const char kChromeUIUntrustedHatsURL[];
#endif  // !BUILDFLAG(IS_ANDROID)

}  // namespace chrome

#endif  // CHROME_COMMON_WEBUI_URL_CONSTANTS_H_
