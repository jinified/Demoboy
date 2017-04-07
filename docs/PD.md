# Spatial effect

## Panning
1. `stereo_pan`= offset from neutral position [0 - 1]

## Binaural
1. `azimuth` = [0-360]
2. `elevation` = [-40-90]

# Effects parameters
To activate an effect, please use `PdBase.sendFloat("effect_id", id)` where id corresponds with the number beside each effect.

## Vibrato [1]
1. `vibrato_freq` = frequency of LFO [1 - 5]
2. `vibrato_delay` = length of delay line [1 - 5]

## Tremolo [2]
1. `tremolo_freq` = frequency of oscillator [0.5 - 25]
2. `tremolo_gain` = gain multiplied to original signal [1 - 20]
3. `tremolo_depth` = depth of modulation [0 - 0.5]

## Delay [3]
1. `delay_length` = length of read delay line [50 - 500]
2. `delay_feedback` = depth of modulation [0-0.8]

## Wha-wha effect [4]
1. `wha_maxfreq` = maximum frequency for the range [700 - 6000]
2. `wha_minfreq` = frequency point that separates into lower and higher range [0 - 700]
3. `wha_q` = filter sharpness [0 - 20]

## Distortion [5]
1. `distortion_clip` = min clip [0.4 - 0.8]
2. `distortion_gain` = gain multiplied to original signal [1 - 20]

## Phaser [6]
1. `phaser_speed` = speed of LFO [0 - 10]
2. `phaser_depth` = depth of delay [0 - 3]
3. `phaser_feedback` = delay feedback [-0.9 - 0.9]
e
## Reverb [7]
1. `reverb_gain` = gain of the feedback [0-100]

# Synthesis parameters

# Ring modulation synthesis [1]
1. `rm_carrier_midinote` = midi note used for carrier [0-127]
2. `rm_mod_midinote` = midi note used for modulator [0-127]

# Amplitude modulation synthesis [2]
1. `am_carrier_midinote` = midi note used for carrier [0-127]
2. `am_mod_midinote` = midi note used for modulator [0-127]

## Frequency modulation Synthesis [3]
1. `fm_carrier_midinote` = midi note used for carrier [0-127]
2. `fm_mod_midinote` = midi note used for modulator [0-127]
2. `fm_modindex` = depth of modulation [10-150]

## Additive Synthesis
1. `n_sines`  = number of sinusoids
2. `f_ratios` = ratio between partials
3. `base_freq` = frequency of first partial
4. `amp_ratios` = amplitude ratio between partials
5. `dur_ratios` = envelope duration ratio between partials

## Subtractive synthesis
1. `waveform` = waveform used for main wave. Noise can be used to generate inharmonic sound.
2. `cutoff_freq` = cut-off frequency used by low-pass filter
3. `resonance` = boosting frequencies around cut-off
4. `filter_slope` = gain reduction per octave after cut-off
5. `adsr` = envelope for signal amplitude
6. `lfo_waveform` = low frequency oscillator's waveform
6. `lfo_freq` = low frequency oscillator's frequency
