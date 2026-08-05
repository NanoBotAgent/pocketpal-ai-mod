import {makeAutoObservable} from 'mobx';

export type SplitPreset =
  | '100-0'
  | '80-20'
  | '50-50'
  | '20-80'
  | '0-100'
  | 'custom';

export interface HardwareSplitConfig {
  preset: SplitPreset;
  gpuRatio: number;
}

const DEFAULT_CONFIG: HardwareSplitConfig = {
  preset: '100-0',
  gpuRatio: 1.0,
};

const PRESET_RATIOS: Record<Exclude<SplitPreset, 'custom'>, number> = {
  '100-0': 1.0,
  '80-20': 0.8,
  '50-50': 0.5,
  '20-80': 0.2,
  '0-100': 0.0,
};

class HardwareSplitStore {
  config: HardwareSplitConfig = DEFAULT_CONFIG;

  constructor() {
    makeAutoObservable(this);
  }

  setPreset(preset: SplitPreset) {
    if (preset !== 'custom') {
      this.config = {preset, gpuRatio: PRESET_RATIOS[preset]};
    } else {
      this.config = {preset, gpuRatio: this.config.gpuRatio};
    }
  }

  setCustomRatio(ratio: number) {
    const clamped = Math.max(0, Math.min(1, ratio));
    const matched = this.matchPreset(clamped);
    this.config = matched
      ? {preset: matched, gpuRatio: clamped}
      : {preset: 'custom', gpuRatio: clamped};
  }

  get gpuPercentage(): number {
    return Math.round(this.config.gpuRatio * 100);
  }

  get cpuPercentage(): number {
    return 100 - this.gpuPercentage;
  }

  computeNGpuLayers(totalLayers: number): number {
    if (totalLayers <= 0) return 0;
    return Math.round(totalLayers * this.config.gpuRatio);
  }

  private matchPreset(ratio: number): SplitPreset | null {
    for (const [preset, value] of Object.entries(PRESET_RATIOS)) {
      if (Math.abs(ratio - value) < 0.01) {
        return preset as SplitPreset;
      }
    }
    return null;
  }
}

export const hardwareSplitStore = new HardwareSplitStore();
