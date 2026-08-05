import {StyleSheet} from 'react-native';

import type {Theme} from '../../../utils/types';
import {FONT_FAMILIES} from '../../../theme/tokens/typography';

export const createStyles = (theme: Theme) => {
  const isSpaceGrotesk = theme.typography.headlineH1.fontFamily === FONT_FAMILIES.SPACE_GROTESK_BOLD;
  return StyleSheet.create({
    header: {
      width: 369,
      alignItems: 'center',
      gap: theme.spacing.sm,
    },
    title: {
      fontFamily: isSpaceGrotesk
        ? FONT_FAMILIES.SPACE_GROTESK_SEMIBOLD
        : FONT_FAMILIES.MANROPE_BOLD,
      fontSize: 24,
      lineHeight: 28,
      color: theme.colors.onBackground,
      textAlign: 'center',
      width: 279,
    },
    body: {
      ...theme.typography.bodyS,
      color: theme.colors.onSurfaceVariant,
      textAlign: 'center',
    },
  });
};
