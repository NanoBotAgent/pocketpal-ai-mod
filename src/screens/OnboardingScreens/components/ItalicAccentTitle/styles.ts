import {StyleSheet} from 'react-native';

import type {Theme} from '../../../../utils/types';
import {FONT_FAMILIES} from '../../../../theme/tokens/typography';

export const createStyles = (theme: Theme, align: 'left' | 'center') => {
  // headlineH1 binds to SpaceGrotesk-Bold on Latin locales; non-Latin
  // locales fall back to Manrope-Bold via `typographyForLocale`. Use
  // family identity to pick the right italic-run cut.
  const isSpaceGrotesk =
    theme.typography.headlineH1.fontFamily === FONT_FAMILIES.SPACE_GROTESK_BOLD;
  return StyleSheet.create({
    root: {
      alignItems: align === 'center' ? 'center' : 'flex-start',
    },
    title: {
      ...theme.typography.headlineH1,
      color: theme.colors.onBackground,
      textAlign: align,
    },
    italic: isSpaceGrotesk
      ? {fontFamily: FONT_FAMILIES.SPACE_GROTESK_SEMIBOLD, fontStyle: 'italic'}
      : {fontFamily: FONT_FAMILIES.MANROPE_BOLD, fontStyle: 'italic'},
  });
};
