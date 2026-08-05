/**
 * NebulaAI Color tokens — redesigned dark-first palette.
 *
 * Design system: slate-* gray scale, body bg #020617,
 * emerald-400/500 primary accent, glassmorphism surfaces.
 */
import {withOpacity, stateLayerOpacity} from '../../utils/colorUtils';

import {TokenColors} from './types';

// Light base colors — NebulaAI design system (light mode)
const LIGHT_PRIMARY = '#0f172a';
const LIGHT_SECONDARY = '#059669';
const LIGHT_TERTIARY = '#34d399';
const LIGHT_ERROR = '#ef4444';
const LIGHT_BACKGROUND = '#f8fafc';
const LIGHT_ON_BACKGROUND = '#0f172a';
const LIGHT_SURFACE = '#ffffff';
const LIGHT_ON_SURFACE = '#1e293b';
const LIGHT_INVERSE_ON_SURFACE = '#f1f5f9';

export const lightColors: TokenColors = {
  // NebulaAI base palette (light)
  primary: LIGHT_PRIMARY,
  onPrimary: '#f8fafc',
  primaryContainer: '#e2e8f0',
  onPrimaryContainer: '#0f172a',
  secondary: LIGHT_SECONDARY,
  onSecondary: '#ffffff',
  secondaryContainer: '#d1fae5',
  onSecondaryContainer: '#064e3b',
  tertiary: LIGHT_TERTIARY,
  onTertiary: '#ffffff',
  tertiaryContainer: '#ecfdf5',
  onTertiaryContainer: '#022c22',
  error: LIGHT_ERROR,
  onError: '#ffffff',
  errorContainer: '#fee2e2',
  onErrorContainer: '#7f1d1d',
  background: LIGHT_BACKGROUND,
  onBackground: LIGHT_ON_BACKGROUND,
  surface: LIGHT_SURFACE,
  onSurface: LIGHT_ON_SURFACE,
  surfaceVariant: '#e2e8f0',
  onSurfaceVariant: '#64748b',
  outline: withOpacity(LIGHT_PRIMARY, 0.1),
  outlineVariant: '#cbd5e1',
  mutedLight: '#e5e3e1',
  secondaryDefault: '#f1f5f9',
  // MD3 extras
  surfaceDisabled: withOpacity('#f8fafc', 0.12),
  onSurfaceDisabled: withOpacity('#1e293b', 0.38),
  inverseSurface: '#1e293b',
  inverseOnSurface: LIGHT_INVERSE_ON_SURFACE,
  inversePrimary: '#e2e8f0',
  inverseSecondary: '#95ABE6',
  shadow: '#000000',
  scrim: 'rgba(0, 0, 0, 0.25)',
  backdrop: 'rgba(15, 23, 42, 0.6)',

  // Glassmorphism surface variants (light)
  surfaceContainerHighest: withOpacity('#e2e8f0', 0.95),
  surfaceContainerHigh: withOpacity('#e2e8f0', 0.85),
  surfaceContainer: withOpacity('#f1f5f9', 0.75),
  surfaceContainerLow: withOpacity('#f8fafc', 0.65),
  surfaceContainerLowest: LIGHT_SURFACE,
  surfaceDim: withOpacity('#cbd5e1', 0.5),
  surfaceBright: '#ffffff',

  // Text
  text: LIGHT_ON_BACKGROUND,
  textSecondary: '#64748b',
  inverseText: LIGHT_INVERSE_ON_SURFACE,
  inverseTextSecondary: withOpacity(LIGHT_INVERSE_ON_SURFACE, 0.5),

  // Border / placeholder
  border: '#e2e8f0',
  placeholder: withOpacity(LIGHT_ON_SURFACE, 0.3),

  // Interactive state opacities
  stateLayerOpacity: 0.12,
  hoverStateOpacity: stateLayerOpacity.hover,
  pressedStateOpacity: stateLayerOpacity.pressed,
  draggedStateOpacity: stateLayerOpacity.dragged,
  focusStateOpacity: stateLayerOpacity.focus,

  // Menu
  menuBackground: '#f8fafc',
  menuBackgroundDimmed: withOpacity(LIGHT_SURFACE, 0.9),
  menuBackgroundActive: withOpacity('#059669', 0.08),
  menuSeparator: withOpacity(LIGHT_PRIMARY, 0.5),
  menuGroupSeparator: withOpacity('#000000', 0.08),
  menuText: LIGHT_ON_SURFACE,
  menuDangerText: LIGHT_ERROR,

  // Messages
  authorBubbleBackground: '#f1f5f9',
  receivedMessageDocumentIcon: LIGHT_PRIMARY,
  sentMessageDocumentIcon: LIGHT_ON_SURFACE,
  userAvatarImageBackground: 'transparent',
  userAvatarNameColors: ['#059669', '#0ea5e9', '#8b5cf6', LIGHT_ERROR],
  searchBarBackground: 'rgba(15, 23, 42, 0.08)',

  // Thinking bubble
  thinkingBubbleBackground: '#ecfdf5',
  thinkingBubbleText: '#047857',
  thinkingBubbleBorder: 'rgba(5, 150, 105, 0.4)',
  thinkingBubbleShadow: '#10b981',
  thinkingBubbleChevronBackground: 'rgba(5, 150, 105, 0.1)',
  thinkingBubbleChevronBorder: 'rgba(5, 150, 105, 0.2)',

  // Status bar
  bgStatusActive: '#10b981',
  bgStatusIdle: '#cbd5e1',

  // Buttons
  btnPrimaryBg: '#ecfdf5',
  btnPrimaryBorder: '#a7f3d0',
  btnPrimaryText: '#047857',
  btnReadyBg: '#ecfdf5',
  btnReadyBorder: '#a7f3d0',
  btnReadyText: '#047857',
  btnDownloadBg: '#ecfdf5',
  btnDownloadBorder: '#a7f3d0',
  btnDownloadText: '#047857',

  // Icons
  iconModelTypeText: '#0ea5e9',
  iconModelTypeVision: '#8b5cf6',
  iconModelTypeAudio: '#f97316',

  // Accent
  accent: {
    peach: '#FCE7CF',
    greenStrong: '#10b981',
  },
};

// NebulaAI dark-first palette — slate gray scale, emerald accent
const DARK_PRIMARY = '#e2e8f0';
const DARK_SECONDARY = '#10b981';
const DARK_TERTIARY = '#34d399';
const DARK_ERROR = '#ef4444';
const DARK_BACKGROUND = '#020617';
const DARK_ON_BACKGROUND = '#e2e8f0';
const DARK_SURFACE = '#0f172a';
const DARK_ON_SURFACE = '#cbd5e1';
const DARK_INVERSE_ON_SURFACE = '#1e293b';

export const darkColors: TokenColors = {
  primary: DARK_PRIMARY,
  onPrimary: '#0f172a',
  primaryContainer: '#1e293b',
  onPrimaryContainer: '#e2e8f0',
  secondary: DARK_SECONDARY,
  onSecondary: '#0f172a',
  secondaryContainer: '#064e3b',
  onSecondaryContainer: '#a7f3d0',
  tertiary: DARK_TERTIARY,
  onTertiary: '#022c22',
  tertiaryContainer: '#065f46',
  onTertiaryContainer: '#a7f3d0',
  error: DARK_ERROR,
  onError: '#450a0a',
  errorContainer: '#7f1d1d',
  onErrorContainer: '#fecaca',
  background: DARK_BACKGROUND,
  onBackground: DARK_ON_BACKGROUND,
  surface: DARK_SURFACE,
  onSurface: DARK_ON_SURFACE,
  surfaceVariant: '#334155',
  onSurfaceVariant: '#94a3b8',
  outline: '#334155',
  outlineVariant: '#475569',
  mutedLight: '#1e293b',
  secondaryDefault: '#1e293b',
  surfaceDisabled: withOpacity('#1e293b', 0.12),
  onSurfaceDisabled: withOpacity('#cbd5e1', 0.38),
  inverseSurface: '#e2e8f0',
  inverseOnSurface: DARK_INVERSE_ON_SURFACE,
  inversePrimary: '#1e293b',
  inverseSecondary: LIGHT_SECONDARY,
  shadow: '#000000',
  scrim: 'rgba(0, 0, 0, 0.5)',
  backdrop: 'rgba(2, 6, 23, 0.8)',
  surfaceContainerHighest: 'rgba(30, 41, 59, 0.9)',
  surfaceContainerHigh: 'rgba(30, 41, 59, 0.8)',
  surfaceContainer: 'rgba(30, 41, 59, 0.5)',
  surfaceContainerLow: 'rgba(15, 23, 42, 0.4)',
  surfaceContainerLowest: 'rgba(2, 6, 23, 0.5)',
  surfaceDim: 'rgba(15, 23, 42, 0.15)',
  surfaceBright: '#1e293b',

  // Text
  text: DARK_ON_BACKGROUND,
  textSecondary: '#64748b',
  inverseText: DARK_INVERSE_ON_SURFACE,
  inverseTextSecondary: withOpacity(DARK_INVERSE_ON_SURFACE, 0.5),

  // Border / placeholder
  border: '#334155',
  placeholder: withOpacity(DARK_ON_SURFACE, 0.3),

  // Interactive state opacities
  stateLayerOpacity: 0.12,
  hoverStateOpacity: stateLayerOpacity.hover,
  pressedStateOpacity: stateLayerOpacity.pressed,
  draggedStateOpacity: stateLayerOpacity.dragged,
  focusStateOpacity: stateLayerOpacity.focus,

  // Menu
  menuBackground: '#1e293b',
  menuBackgroundDimmed: withOpacity(DARK_SURFACE, 0.9),
  menuBackgroundActive: withOpacity('#10b981', 0.12),
  menuSeparator: withOpacity(DARK_PRIMARY, 0.5),
  menuGroupSeparator: withOpacity('#ffffff', 0.08),
  menuText: DARK_ON_SURFACE,
  menuDangerText: DARK_ERROR,

  // Messages
  authorBubbleBackground: '#1e293b',
  receivedMessageDocumentIcon: DARK_PRIMARY,
  sentMessageDocumentIcon: DARK_ON_SURFACE,
  userAvatarImageBackground: 'transparent',
  userAvatarNameColors: [
    '#10b981',
    '#0ea5e9',
    '#8b5cf6',
    DARK_ERROR,
  ],
  searchBarBackground: 'rgba(15, 23, 42, 0.6)',

  // Thinking bubble
  thinkingBubbleBackground: '#064e3b',
  thinkingBubbleText: '#6ee7b7',
  thinkingBubbleBorder: 'rgba(52, 211, 153, 0.5)',
  thinkingBubbleShadow: '#34d399',
  thinkingBubbleChevronBackground: 'rgba(52, 211, 153, 0.12)',
  thinkingBubbleChevronBorder: 'rgba(52, 211, 153, 0.2)',

  // Status bar
  bgStatusActive: '#10b981',
  bgStatusIdle: '#475569',

  // Buttons
  btnPrimaryBg: '#064e3b',
  btnPrimaryBorder: '#065f46',
  btnPrimaryText: '#6ee7b7',
  btnReadyBg: '#064e3b',
  btnReadyBorder: '#065f46',
  btnReadyText: '#6ee7b7',
  btnDownloadBg: '#064e3b',
  btnDownloadBorder: '#065f46',
  btnDownloadText: '#6ee7b7',

  // Icons
  iconModelTypeText: '#38bdf8',
  iconModelTypeVision: '#a78bfa',
  iconModelTypeAudio: '#fb923c',

  // Accent
  accent: {
    peach: '#7A4A1F',
    greenStrong: '#10b981',
  },
};
