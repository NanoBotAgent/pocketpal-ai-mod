import React from 'react';
import {View, Text, TouchableOpacity, StyleSheet, ViewStyle} from 'react-native';
import {observer} from 'mobx-react-lite';
import {useTheme} from 'react-native-paper';
import {hardwareSplitStore, SplitPreset} from '../store/HardwareSplitStore';

const PRESETS: {key: SplitPreset; label: string; gpu: number; cpu: number}[] = [
  {key: '100-0', label: '100% GPU', gpu: 100, cpu: 0},
  {key: '80-20', label: '80/20', gpu: 80, cpu: 20},
  {key: '50-50', label: '50/50', gpu: 50, cpu: 50},
  {key: '20-80', label: '20/80', gpu: 20, cpu: 80},
  {key: '0-100', label: '100% CPU', gpu: 0, cpu: 100},
];

export const HardwareSplitSettings = observer(() => {
  const theme = useTheme();
  const c = theme.colors;

  const titleStyle = [styles.title, styles.titleText, {color: c.onSurface}];
  const subtitleStyle = [
    styles.subtitle,
    styles.subtitleText,
    {color: c.onSurfaceVariant},
  ];

  return (
    <View style={[styles.container, {backgroundColor: c.surface}]}>
      <Text style={titleStyle}>GPU / CPU Split</Text>
      <Text style={subtitleStyle}>
        Control how much inference runs on GPU vs CPU
      </Text>

      <View style={styles.presetRow}>
        {PRESETS.map(preset => {
          const active = hardwareSplitStore.config.preset === preset.key;
          return (
            <TouchableOpacity
              key={preset.key}
              onPress={() => hardwareSplitStore.setPreset(preset.key)}
              style={[
                styles.presetButton,
                {
                  backgroundColor: active
                    ? c.secondaryContainer
                    : c.surfaceContainer,
                  borderColor: active ? c.secondary : c.outline,
                },
              ]}>
              <Text
                style={[
                  styles.presetLabel,
                  styles.presetLabelText,
                  {
                    color: active ? c.onSecondaryContainer : c.onSurfaceVariant,
                  },
                ]}>
                {preset.label}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>

      <View style={[styles.ratioBar, {backgroundColor: c.surfaceContainer}]}>
        <View
          style={[
            styles.gpuBar,
            {
              width: `${hardwareSplitStore.gpuPercentage}%`,
              backgroundColor: c.secondary,
            },
          ]}
        />
        <View style={styles.ratioLabels}>
          <Text
            style={[
              styles.ratioText,
              styles.ratioGpu,
              {color: c.secondary},
            ]}>
            GPU {hardwareSplitStore.gpuPercentage}%
          </Text>
          <Text
            style={[
              styles.ratioText,
              styles.ratioCpu,
              {color: c.onSurfaceVariant},
            ]}>
            CPU {hardwareSplitStore.cpuPercentage}%
          </Text>
        </View>
      </View>

      <View style={styles.sliderContainer}>
        <Text
          style={[
            styles.sliderLabel,
            styles.sliderLabelText,
            {color: c.onSurfaceVariant},
          ]}>
          Custom: {hardwareSplitStore.gpuPercentage}% GPU
        </Text>
        <View
          style={[
            styles.sliderTrack,
            {backgroundColor: c.surfaceContainerHighest},
          ]}>
          <View
            style={[
              styles.sliderFill,
              {
                width: `${hardwareSplitStore.gpuPercentage}%`,
                backgroundColor: c.secondary,
              },
            ]}
          />
          {[0, 20, 40, 50, 60, 80, 100].map(pos => (
            <TouchableOpacity
              key={pos}
              onPress={() => hardwareSplitStore.setCustomRatio(pos / 100)}
              style={[
                styles.sliderTick,
                {
                  left: `${pos}%`,
                  backgroundColor:
                    Math.round(hardwareSplitStore.config.gpuRatio * 100) === pos
                      ? c.secondary
                      : c.outline,
                },
              ]}
            />
          ))}
        </View>
      </View>
    </View>
  );
});

const styles = StyleSheet.create({
  container: {
    borderRadius: 16,
    padding: 20,
    marginHorizontal: 16,
    marginVertical: 8,
  } as ViewStyle,
  title: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 4,
  } as ViewStyle,
  titleText: {
    fontFamily: 'SpaceGrotesk-SemiBold',
  } as ViewStyle,
  subtitle: {
    fontSize: 14,
    marginBottom: 16,
  } as ViewStyle,
  subtitleText: {
    fontFamily: 'Manrope-Regular',
  } as ViewStyle,
  presetRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginBottom: 20,
  } as ViewStyle,
  presetButton: {
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: 12,
    borderWidth: 1.5,
  } as ViewStyle,
  presetLabel: {
    fontSize: 13,
    fontWeight: '500',
  } as ViewStyle,
  presetLabelText: {
    fontFamily: 'Manrope-Medium',
  } as ViewStyle,
  ratioBar: {
    height: 40,
    borderRadius: 10,
    overflow: 'hidden',
    marginBottom: 16,
    position: 'relative',
  } as ViewStyle,
  gpuBar: {
    height: '100%',
    borderRadius: 10,
  } as ViewStyle,
  ratioLabels: {
    position: 'absolute',
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '100%',
    height: '100%',
    alignItems: 'center',
    paddingHorizontal: 12,
  } as ViewStyle,
  ratioText: {
    fontSize: 12,
    fontWeight: '600',
  } as ViewStyle,
  ratioGpu: {
    fontFamily: 'JetBrainsMono-Medium',
  } as ViewStyle,
  ratioCpu: {
    fontFamily: 'JetBrainsMono-Medium',
  } as ViewStyle,
  sliderContainer: {
    marginTop: 8,
  } as ViewStyle,
  sliderLabel: {
    fontSize: 13,
    marginBottom: 8,
  } as ViewStyle,
  sliderLabelText: {
    fontFamily: 'Manrope-Regular',
  } as ViewStyle,
  sliderTrack: {
    height: 28,
    borderRadius: 14,
    position: 'relative',
    overflow: 'hidden',
  } as ViewStyle,
  sliderFill: {
    height: '100%',
    borderRadius: 14,
    position: 'absolute',
    left: 0,
    top: 0,
  } as ViewStyle,
  sliderTick: {
    position: 'absolute',
    width: 10,
    height: 10,
    borderRadius: 5,
    top: 9,
    marginLeft: -5,
  } as ViewStyle,
});
