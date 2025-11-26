package com.example.comp1786_logbook_ex1_calculatorapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Main activity controlling UI and delegating math to Calculator. */
public class MainActivity extends AppCompatActivity {

    private TextView txtExpression, txtResult;
    private GridLayout grid;

    // Animations
    private Animation numberEntryAnim;
    private Animation resultRevealAnim;
    private Animation errorShakeAnim;
    private Animation clearRotateAnim;
    private Animation buttonPressAnim;
    private Animation buttonReleaseAnim;

    // Haptic feedback
    private Vibrator vibrator;

    // Background slideshow
    private static final long INTERVAL_MS = 5_000;
    private static final long FADE_MS = 600;
    private final int[] BACKGROUNDS = new int[] {
            R.drawable.bg_leaves,
            R.drawable.bgimage,
            R.drawable.bgimage2
    };
    private ImageView bg1, bg2, ivBlur;
    private boolean showingFirst = true;
    private int index = 0;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable switchTask = new Runnable() {
        @Override public void run() {
            if (!isFinishing()) {
                crossfadeToNext();
                handler.postDelayed(this, INTERVAL_MS);
            }
        }
    };

    // Calculator state
    private String firstOperand = null;
    private String currentOp = null; // "+", "−", "×", "÷"
    private final StringBuilder input = new StringBuilder();
    private boolean computed = false;

    // UI state
    private View lastOperatorButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtExpression = findViewById(R.id.txtExpression);
        txtResult = findViewById(R.id.txtResult);
        grid = findViewById(R.id.grid);

        // Load animations
        numberEntryAnim = AnimationUtils.loadAnimation(this, R.anim.number_entry);
        resultRevealAnim = AnimationUtils.loadAnimation(this, R.anim.result_reveal);
        errorShakeAnim = AnimationUtils.loadAnimation(this, R.anim.error_shake);
        clearRotateAnim = AnimationUtils.loadAnimation(this, R.anim.clear_button_rotate);
        buttonPressAnim = AnimationUtils.loadAnimation(this, R.anim.button_press);
        buttonReleaseAnim = AnimationUtils.loadAnimation(this, R.anim.button_release);

        // Initialize vibrator for haptic feedback
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setupBackgroundRotation();
        wireKeysFromGrid();
        setupCopyToClipboard();
        updateDisplay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BACKGROUNDS.length > 1) {
            handler.removeCallbacks(switchTask);
            handler.postDelayed(switchTask, INTERVAL_MS);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(switchTask);
    }

    private void setupBackgroundRotation() {
        bg1 = findViewById(R.id.bgImage);
        bg2 = findViewById(R.id.bgImage2);
        ivBlur = findViewById(R.id.ivBlur);

        if (BACKGROUNDS.length > 0) {
            index = 0;
            if (bg1 != null) bg1.setImageResource(BACKGROUNDS[index]);
            if (ivBlur != null) {
                ivBlur.setImageResource(BACKGROUNDS[index]);
                applyBlurIfSupported(ivBlur);
            }
        }
    }

    private void crossfadeToNext() {
        if (BACKGROUNDS.length <= 1) return;

        index = (index + 1) % BACKGROUNDS.length;
        final ImageView fadeOut = showingFirst ? bg1 : bg2;
        final ImageView fadeIn = showingFirst ? bg2 : bg1;
        if (fadeIn == null || fadeOut == null) return;

        fadeIn.setImageResource(BACKGROUNDS[index]);
        fadeIn.setAlpha(0f);
        fadeIn.animate().alpha(1f).setDuration(FADE_MS).start();
        fadeOut.animate().alpha(0f).setDuration(FADE_MS).start();

        if (ivBlur != null) {
            ivBlur.setImageResource(BACKGROUNDS[index]);
            applyBlurIfSupported(ivBlur);
        }
        showingFirst = !showingFirst;
    }

    private void applyBlurIfSupported(ImageView target) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            target.setRenderEffect(RenderEffect.createBlurEffect(28f, 28f, Shader.TileMode.CLAMP));
        }
    }

    private void setupCopyToClipboard() {
        // Long press on result: if empty or "0" try paste immediately; otherwise show explicit menu (Paste/Copy)
        txtResult.setOnLongClickListener(v -> {
            String rawText = txtResult.getText().toString();
            String text = rawText == null ? "" : rawText.trim();

            // If empty or zero, try paste first so user can long-press to paste quickly
            if (text.isEmpty() || text.equals("0")) {
                boolean pasted = performPasteFromClipboard();
                if (pasted) return true; // success
                // else fallthrough to show menu so user can attempt copy/paste manually
            }

            PopupMenu menu = new PopupMenu(MainActivity.this, txtResult);
            menu.getMenu().add("Paste");
            menu.getMenu().add("Copy");
            menu.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                if ("Paste".equals(title)) {
                    boolean ok = performPasteFromClipboard();
                    if (!ok) toast(getString(R.string.msg_nothing_to_copy));
                    return true;
                } else if ("Copy".equals(title)) {
                    performCopyFromResult();
                    return true;
                }
                return false;
            });
            menu.show();
            return true;
        });

        // Long press on expression to copy
        txtExpression.setOnLongClickListener(v -> {
            String text = txtExpression.getText().toString();
            if (text.isEmpty()) {
                toast(getString(R.string.msg_nothing_to_copy));
                return true;
            }

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("calculator_expression", text);
            clipboard.setPrimaryClip(clip);

            toast(getString(R.string.msg_expression_copied));
            performHapticFeedback(HapticFeedbackType.MEDIUM);
            return true;
        });
    }

    /**
     * Try to parse an expression string of the form "<a> <op> <b>" and set internal state.
     * Returns true if parsing succeeded and state updated.
     */
    private boolean tryParseAndSetExpression(String expr) {
        if (expr == null) return false;
        String t = expr.trim();
        if (t.isEmpty()) return false;
        // tokens separated by spaces; expression constructed in updateDisplay uses spaces
        String[] parts = t.split(" ");
        if (parts.length < 3) return false;
        // First token may itself contain grouping commas etc. Join tokens for b if extra spaces
        String aStr = parts[0];
        String op = parts[1];
        StringBuilder bSb = new StringBuilder();
        for (int i = 2; i < parts.length; i++) {
            if (i > 2) bSb.append(' ');
            bSb.append(parts[i]);
        }
        String bStr = bSb.toString();

        // Normalize operator to one of the app's operators
        String add = getString(R.string.key_add);
        String sub = getString(R.string.key_sub);
        String mul = getString(R.string.key_mul);
        String div = getString(R.string.key_divide);
        String normOp = null;
        if (op.equals(add) || op.equals("+")) normOp = add;
        else if (op.equals(sub) || op.equals("-") || op.equals("−")) normOp = sub;
        else if (op.equals(mul) || op.equals("x") || op.equals("X") || op.equals("×")) normOp = mul;
        else if (op.equals(div) || op.equals("/") || op.equals("÷")) normOp = div;
        if (normOp == null) return false;

        // Clean numeric strings
        aStr = aStr.replace(",", "");
        bStr = bStr.replace(",", "");
        try {
            // validate parsing
            Calculator.parse(aStr);
            Calculator.parse(bStr);
            firstOperand = Calculator.sanitizeNumber(aStr);
            currentOp = normOp;
            input.setLength(0);
            input.append(Calculator.sanitizeNumber(bStr));
            computed = false;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private void wireKeysFromGrid() {
        int n = grid.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = grid.getChildAt(i);
            if (v instanceof TextView) {
                final TextView btn = (TextView) v;
                btn.setClickable(true);
                btn.setOnClickListener(view -> {
                    // Animate button press
                    animateButtonPress(btn);
                    onKey(btn.getText().toString());
                });
            }
        }
    }

    private void animateButtonPress(View button) {
        button.startAnimation(buttonPressAnim);
        button.postDelayed(() -> button.startAnimation(buttonReleaseAnim), 100);
        // Light haptic feedback for button press
        performHapticFeedback(HapticFeedbackType.LIGHT);
    }

    // Haptic feedback helper methods
    private enum HapticFeedbackType {
        LIGHT,    // For normal button presses
        MEDIUM,   // For operators
        HEAVY     // For errors
    }

    private void performHapticFeedback(HapticFeedbackType type) {
        if (vibrator == null || !vibrator.hasVibrator()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect;
            switch (type) {
                case LIGHT:
                    effect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE);
                    break;
                case MEDIUM:
                    effect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE);
                    break;
                case HEAVY:
                    effect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE);
                    break;
                default:
                    effect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE);
            }
            vibrator.vibrate(effect);
        } else {
            // Fallback for older devices
            switch (type) {
                case LIGHT:
                    vibrator.vibrate(10);
                    break;
                case MEDIUM:
                    vibrator.vibrate(20);
                    break;
                case HEAVY:
                    vibrator.vibrate(50);
                    break;
            }
        }
    }

    private void onKey(String key) {
        String clear = getString(R.string.key_clear);
        String back = getString(R.string.key_backspace);
        String pct = getString(R.string.key_percent);
        String eq = getString(R.string.key_equals);
        String plusMinus = getString(R.string.key_plusminus);

        if (key.equals(clear)) {
            // Find and animate the clear button
            animateClearButton();
            performHapticFeedback(HapticFeedbackType.MEDIUM);
            clearAll();
            return;
        }
        if (key.equals(back)) { backspace(); return; }
        if (key.equals(pct)) { percent(); return; }
        if (key.equals(eq)) { equalsCompute(); return; }
        if (key.equals(plusMinus)) { toggleSign(); return; }

        if (key.equals(getString(R.string.key_add)) || key.equals(getString(R.string.key_sub))
                || key.equals(getString(R.string.key_mul)) || key.equals(getString(R.string.key_divide))) {
            setOperator(key); return;
        }
        if (key.equals(getString(R.string.key_dot))) { appendDot(); return; }

        if (key.length() == 1 && Character.isDigit(key.charAt(0))) {
            appendDigit(key.charAt(0)); return;
        }
        toast(String.format(getString(R.string.msg_key_not_supported), key));
    }

    private void animateClearButton() {
        // Find the clear button in grid and animate it
        String clearKey = getString(R.string.key_clear);
        for (int i = 0; i < grid.getChildCount(); i++) {
            View v = grid.getChildAt(i);
            if (v instanceof TextView) {
                TextView btn = (TextView) v;
                if (clearKey.equals(btn.getText().toString())) {
                    btn.startAnimation(clearRotateAnim);
                    break;
                }
            }
        }
    }

    private void toggleSign() {
        if (input.length() > 0) {
            if (input.charAt(0) == '-') input.deleteCharAt(0);
            else input.insert(0, '-');
        } else if (firstOperand != null && currentOp == null) {
            if (firstOperand.startsWith("-")) firstOperand = firstOperand.substring(1);
            else firstOperand = "-" + firstOperand;
        }
        updateDisplay();
    }

    private void appendDigit(char d) {
        if (computed) clearAll();
        if (input.length() == 1 && input.charAt(0) == '0') {
            if (d != '0') input.setCharAt(0, d);
        } else {
            input.append(d);
        }
        updateDisplay();
        // Animate number entry
        txtResult.startAnimation(numberEntryAnim);
    }

    private void appendDot() {
        if (computed) clearAll();
        if (TextUtils.indexOf(input, ".") >= 0) return;
        if (input.length() == 0) input.append("0.");
        else input.append('.');
        updateDisplay();
        // Animate number entry
        txtResult.startAnimation(numberEntryAnim);
    }

    private void setOperator(String op) {
        if (input.length() == 0 && firstOperand == null) {
            firstOperand = "0";
        } else if (firstOperand == null) {
            firstOperand = Calculator.sanitizeNumber(input.toString());
            input.setLength(0);
        } else if (currentOp != null && input.length() > 0) {
            equalsCompute(false);
        }
        currentOp = op;
        computed = false;

        // Highlight the operator button
        highlightOperatorButton(op);

        updateDisplay();
    }

    private void highlightOperatorButton(String op) {
        // Reset previous operator button
        if (lastOperatorButton != null) {
            lastOperatorButton.setAlpha(1.0f);
            lastOperatorButton = null;
        }

        // Find and highlight current operator button
        for (int i = 0; i < grid.getChildCount(); i++) {
            View v = grid.getChildAt(i);
            if (v instanceof TextView) {
                TextView btn = (TextView) v;
                if (op.equals(btn.getText().toString())) {
                    btn.setAlpha(0.6f);
                    lastOperatorButton = btn;
                    break;
                }
            }
        }
    }

    private void backspace() {
        if (input.length() > 0)      input.deleteCharAt(input.length() - 1);
        else if (currentOp != null)  currentOp = null;
        else if (computed)           clearAll();
        updateDisplay();
    }

    private void percent() {
        if (input.length() == 0) return;
        try {
            BigDecimal x = Calculator.parse(input.toString());
            x = x.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).stripTrailingZeros();
            input.setLength(0);
            input.append(x.toPlainString());
            updateDisplay();
        } catch (NumberFormatException ex) {
            toast(getString(R.string.msg_invalid_number));
        }
    }

    private void equalsCompute() { equalsCompute(true); }

    private void equalsCompute(boolean showEquals) {
        if (currentOp == null) {
            if (input.length() == 0 && firstOperand != null) computed = true;
            updateDisplay(); return;
        }
        String aStr = (firstOperand != null) ? firstOperand : "0";
        String bStr = (input.length() > 0) ? input.toString() : null;
        if (bStr == null) {
            toast(getString(R.string.msg_enter_second_number));
            // Shake on error
            txtResult.startAnimation(errorShakeAnim);
            performHapticFeedback(HapticFeedbackType.HEAVY);
            return;
        }

        Calculator.Result r = Calculator.compute(aStr, bStr, currentOp);
        if (!r.ok) {
            // Shake on error
            txtResult.startAnimation(errorShakeAnim);
            performHapticFeedback(HapticFeedbackType.HEAVY);
            switch (r.error) {
                case "divide_by_zero": toast(getString(R.string.msg_divide_by_zero)); return;
                case "invalid_a": case "invalid_b": toast(getString(R.string.msg_invalid_number)); return;
                default: toast(getString(R.string.msg_invalid_number)); return;
            }
        }

        firstOperand = r.value.toPlainString();
        currentOp = null;
        input.setLength(0);
        computed = showEquals;

        // Reset operator highlight after computation
        if (lastOperatorButton != null) {
            lastOperatorButton.setAlpha(1.0f);
            lastOperatorButton = null;
        }

        updateDisplay();

        // Animate result reveal
        if (showEquals) {
            txtResult.startAnimation(resultRevealAnim);
            performHapticFeedback(HapticFeedbackType.MEDIUM);
        }
    }

    private void clearAll() {
        input.setLength(0);
        firstOperand = null;
        currentOp = null;
        computed = false;

        // Reset operator highlight
        if (lastOperatorButton != null) {
            lastOperatorButton.setAlpha(1.0f);
            lastOperatorButton = null;
        }

        updateDisplay();
    }

    private void updateDisplay() {
        if (firstOperand == null && currentOp == null) {
            txtExpression.setText("");
        } else if (currentOp == null) {
            txtExpression.setText(Calculator.formatStringSafe(firstOperand));
        } else {
            String b = (input.length() > 0) ? Calculator.sanitizeNumber(input.toString()) : "";
            String expr = (firstOperand == null ? "" : Calculator.formatStringSafe(firstOperand))
                    + (currentOp == null ? "" : " " + currentOp)
                    + (b.isEmpty() ? "" : " " + Calculator.formatStringSafe(b));
            txtExpression.setText(expr);
        }

        if (computed) {
            txtResult.setText(String.format(getString(R.string.result_equals), Calculator.formatStringSafe(firstOperand)));
            txtResult.setAlpha(1.0f);
        } else if (currentOp == null) {
            String disp = (input.length() > 0) ? Calculator.formatStringSafe(input.toString())
                    : (firstOperand != null ? Calculator.formatStringSafe(firstOperand) : "0");
            txtResult.setText(disp);
            txtResult.setAlpha(1.0f);
        } else {
            // Show live preview when operator is set and user is typing second operand
            if (input.length() > 0) {
                try {
                    String aStr = (firstOperand != null) ? firstOperand : "0";
                    Calculator.Result preview = Calculator.compute(aStr, input.toString(), currentOp);
                    if (preview.ok) {
                        // Show preview with "= " prefix and dimmed
                        txtResult.setText("= " + Calculator.format(preview.value));
                        txtResult.setAlpha(0.5f);
                    } else {
                        // Show current input if preview fails
                        txtResult.setText(Calculator.formatStringSafe(input.toString()));
                        txtResult.setAlpha(1.0f);
                    }
                } catch (Exception e) {
                    // Fallback to showing current input
                    txtResult.setText(Calculator.formatStringSafe(input.toString()));
                    txtResult.setAlpha(1.0f);
                }
            } else {
                txtResult.setText("");
                txtResult.setAlpha(1.0f);
            }
        }
    }

    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }

    // Extracted copy behavior (keeps existing logic)
    private void performCopyFromResult() {
        String rawText = txtResult.getText().toString();
        String text = rawText == null ? "" : rawText.trim();

        boolean looksLikeResult = computed || text.startsWith("=") || (!text.isEmpty() && currentOp == null && !text.equals("0"));
        if (looksLikeResult && !text.isEmpty()) {
            if (text.startsWith("= ") && currentOp != null && input.length() > 0) {
                String expr = txtExpression.getText().toString();
                String previewValue = text.substring(2);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("calculator_preview", expr + "\n" + previewValue);
                if (clipboard != null) clipboard.setPrimaryClip(clip);
                toast(getString(R.string.msg_result_copied));
                performHapticFeedback(HapticFeedbackType.MEDIUM);
                return;
            }

            String toCopy = text;
            if (toCopy.startsWith("= ")) toCopy = toCopy.substring(2);
            else if (toCopy.startsWith("=")) toCopy = toCopy.substring(1);
            toCopy = toCopy.trim();

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("calculator_result", toCopy);
            if (clipboard != null) clipboard.setPrimaryClip(clip);

            toast(getString(R.string.msg_result_copied));
            performHapticFeedback(HapticFeedbackType.MEDIUM);
        } else {
            toast(getString(R.string.msg_nothing_to_copy));
        }
    }

    // Extracted paste behavior: attempt to paste regardless of display state (called from menu)
    private boolean performPasteFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null || !clipboard.hasPrimaryClip()) return false;
        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null || clip.getItemCount() == 0) return false;
        CharSequence clipCs = clip.getItemAt(0).coerceToText(MainActivity.this);
        if (clipCs == null) return false;
        String clipText = clipCs.toString().trim();
        if (clipText.isEmpty()) return false;

        // 1) If clip contains a newline, it may be our preview payload: first line = expression
        if (clipText.contains("\n")) {
            String[] lines = clipText.split("\\n", 2);
            String expr = lines.length > 0 ? lines[0].trim() : "";
            if (!expr.isEmpty() && tryParseAndSetExpression(expr)) {
                computed = false;
                updateDisplay();
                toast(getString(R.string.msg_pasted_from_clipboard));
                // Debug
                toast("Paste branch: preview/expression (newline)");
                performHapticFeedback(HapticFeedbackType.MEDIUM);
                return true;
            }
        }

        // 2) If text looks like an inline expression (e.g. "12 + 3"), try to parse
        if (clipText.matches(".*\\s[+\\-×÷xX/*]\\s.*")) {
            if (tryParseAndSetExpression(clipText)) {
                computed = false;
                updateDisplay();
                toast(getString(R.string.msg_pasted_from_clipboard));
                // Debug
                toast("Paste branch: inline expression");
                performHapticFeedback(HapticFeedbackType.MEDIUM);
                return true;
            }
        }

        // 3) Try to treat as a numeric value (final result)
        String clipVal = clipText;
        if (clipVal.startsWith("= ")) clipVal = clipVal.substring(2).trim();
        else if (clipVal.startsWith("=")) clipVal = clipVal.substring(1).trim();
        clipVal = clipVal.replace(",", "");
        try {
            Calculator.parse(clipVal);
            firstOperand = Calculator.sanitizeNumber(clipVal);
            input.setLength(0);
            currentOp = null;
            computed = true;
            updateDisplay();
            toast(getString(R.string.msg_pasted_from_clipboard));
            // Debug
            toast("Paste branch: numeric");
            performHapticFeedback(HapticFeedbackType.MEDIUM);
            return true;
        } catch (Exception ignored) {
            // not a plain numeric value
        }

        // 4) Fallback: check clip description label if present (backwards compatibility)
        CharSequence labelCs = (clip.getDescription() != null) ? clip.getDescription().getLabel() : null;
        String label = labelCs == null ? null : labelCs.toString();
        if ("calculator_preview".equals(label)) {
            String[] lines = clipText.split("\\n", 2);
            String expr = lines.length > 0 ? lines[0].trim() : "";
            if (!expr.isEmpty() && tryParseAndSetExpression(expr)) {
                computed = false;
                updateDisplay();
                toast(getString(R.string.msg_pasted_from_clipboard));
                // Debug
                toast("Paste branch: label=calculator_preview");
                performHapticFeedback(HapticFeedbackType.MEDIUM);
                return true;
            }
        } else if ("calculator_expression".equals(label)) {
            if (tryParseAndSetExpression(clipText)) {
                computed = false;
                updateDisplay();
                toast(getString(R.string.msg_pasted_from_clipboard));
                // Debug
                toast("Paste branch: label=calculator_expression");
                performHapticFeedback(HapticFeedbackType.MEDIUM);
                return true;
            }
        } else if ("calculator_result".equals(label)) {
            String pastedVal = clipText;
            if (pastedVal.startsWith("= ")) pastedVal = pastedVal.substring(2).trim();
            if (pastedVal.startsWith("=")) pastedVal = pastedVal.substring(1).trim();
            pastedVal = pastedVal.replace(",", "");
            try {
                firstOperand = Calculator.sanitizeNumber(pastedVal);
                input.setLength(0);
                currentOp = null;
                computed = true;
                updateDisplay();
                toast(getString(R.string.msg_pasted_from_clipboard));
                // Debug
                toast("Paste branch: label=calculator_result");
                performHapticFeedback(HapticFeedbackType.MEDIUM);
                return true;
            } catch (Exception ignored) {
            }
        }

        // 5) Additional heuristics: normalize connector symbols and try to parse expressions without spaces
        String normalized = clipText.replace('\u2212', '-') // minus sign
                .replace('×', 'x').replace('÷', '/');
        // Try expression without spaces: e.g. "12+3" or "12×3"
        Pattern exprNoSpace = Pattern.compile("\\s*([+-]?[0-9,]*\\.?[0-9]+)\\s*([+\\-xX×\\/÷])\\s*([+-]?[0-9,]*\\.?[0-9]+)\\s*");
        Matcher m = exprNoSpace.matcher(normalized);
        if (m.find()) {
            String a = m.group(1).replace(",", "");
            String op = m.group(2);
            String b = m.group(3).replace(",", "");
            String opSymbol = op;
            // Map to UI operator symbols if necessary
            if (op.equals("x") || op.equals("X") || op.equals("×")) opSymbol = getString(R.string.key_mul);
            else if (op.equals("/") || op.equals("÷")) opSymbol = getString(R.string.key_divide);
            else if (op.equals("+")) opSymbol = getString(R.string.key_add);
            else if (op.equals("-")) opSymbol = getString(R.string.key_sub);
            String expr = a + " " + opSymbol + " " + b;
            if (tryParseAndSetExpression(expr)) {
                computed = false;
                updateDisplay();
                toast(getString(R.string.msg_pasted_from_clipboard));
                toast("Paste branch: expression-no-space");
                performHapticFeedback(HapticFeedbackType.MEDIUM);
                return true;
            }
        }

        // 6) Last resort: extract first numeric token from free text and paste as number
        Pattern numPattern = Pattern.compile("[+-]?\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?|[+-]?\\d+\\.?\\d*");
        Matcher numM = numPattern.matcher(clipText);
        if (numM.find()) {
            String found = numM.group(0).replace(",", "");
            try {
                Calculator.parse(found); // validate
                firstOperand = Calculator.sanitizeNumber(found);
                input.setLength(0);
                currentOp = null;
                computed = true;
                updateDisplay();
                toast(getString(R.string.msg_pasted_from_clipboard));
                toast("Paste branch: extracted-number");
                performHapticFeedback(HapticFeedbackType.MEDIUM);
                return true;
            } catch (Exception ignored) {
            }
        }

         return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(switchTask);
    }
}
